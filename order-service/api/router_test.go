package api_test

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"net/http/httptest"
	"order-service/api"
	"order-service/client"
	"order-service/database"
	"order-service/messaging"
	"order-service/model"
	"reflect"
	"strings"
	"testing"

	"github.com/gin-gonic/gin"
	"github.com/google/jsonapi"
)

func TestShouldReturn4xxCodeForInvalidRequest(t *testing.T) {
	testCases := []struct {
		name           string
		url            string
		expectedStatus int
	}{
		{
			name:           "Invalid query with user=test",
			url:            "/v1/orders/?user=test",
			expectedStatus: http.StatusBadRequest,
		},
		{
			name:           "Invalid query with application=test",
			url:            "/v1/orders/?application=test",
			expectedStatus: http.StatusBadRequest,
		},
		{
			name:           "Invalid user ID",
			url:            "/v1/orders/?user=2",
			expectedStatus: http.StatusForbidden,
		},
		{
			name:           "Invalid application publisher",
			url:            "/v1/orders/?application=1",
			expectedStatus: http.StatusForbidden,
		},
	}
	user := `{"id":1,"name":"","surname":"", "username": "", "role":"", "balance":10}`
	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			router := api.InitRouter(api.AppService{AppClient: &testApplicationClient})
			w := httptest.NewRecorder()
			req, _ := http.NewRequest("GET", tc.url, nil)
			req.Header.Set("grid-user", user)
			router.ServeHTTP(w, req)

			if w.Code != tc.expectedStatus {
				t.Errorf("Unexpected status code: got %d want %d", w.Code, tc.expectedStatus)
			}
		})
	}
}

func TestShouldReturnAllOrders(t *testing.T) {
	testCases := []struct {
		name string
		url  string
		user string
		len  int
	}{
		{
			name: "Should return all orders for user (USER)",
			url:  "/v1/orders/",
			user: `{"id":1,"name":"","surname":"", "username": "", "role":"", "balance":10}`,
			len:  2,
		},
		{
			name: "Should return all orders for user (ADMIN)",
			url:  "/v1/orders/?user=3",
			user: `{"id":1,"name":"","surname":"", "username": "", "role":"ADMIN", "balance":10}`,
			len:  1,
		},
		{
			name: "Should return all orders for application (ADMIN)",
			url:  "/v1/orders/?application=3",
			user: `{"id":1,"name":"","surname":"", "username": "", "role":"ADMIN", "balance":10}`,
			len:  1,
		},
		{
			name: "Should return all orders for application (PUBLISHER)",
			url:  "/v1/orders/?application=1",
			user: `{"id":4,"name":"","surname":"", "username": "", "role":"", "balance":10}`,
			len:  2,
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			database.InitDb(database.InitMockDb())
			router := api.InitRouter(api.AppService{AppClient: &testApplicationClient})
			w := httptest.NewRecorder()
			req, _ := http.NewRequest("GET", tc.url, nil)
			req.Header.Set("grid-user", tc.user)
			router.ServeHTTP(w, req)
			if w.Code != http.StatusOK {
				t.Errorf("Unexpected status code: got %d want %d", w.Code, http.StatusOK)
			}
			result, _ := jsonapi.UnmarshalManyPayload(w.Result().Body, reflect.TypeOf(new(model.Order)))
			actual := len(result)
			if actual != tc.len {
				t.Errorf("Unexpected returned array length: got %d want %d", actual, tc.len)
			}
		})
	}
}

func TestShouldReturn400IfInvalidBody(t *testing.T) {
	router, w, req := setupPostOrdersRouter(map[string]any{"application": "1", "method": "1"}, t)
	router.ServeHTTP(w, req)
	if w.Code != http.StatusBadRequest {
		t.Errorf("Unexpected status code: got %d want %d", w.Code, http.StatusBadRequest)
	}
}

func TestPostOrdersShouldReturn40XForInvalidRequest(t *testing.T) {
	testCases := []struct {
		name             string
		input            map[string]any
		expectedStatus   int
		expectedResponse string
	}{
		{
			name:             "Should return 404 if invalid application",
			input:            map[string]any{"application": 10, "method": 1},
			expectedStatus:   http.StatusNotFound,
			expectedResponse: `{"errors":[{"title":"Not found","detail":"Specified application not found !","status":"404"}]}`,
		},
		{
			name:             "Should return 400 if app owner equals to order requester",
			input:            map[string]any{"application": 3, "method": 1},
			expectedStatus:   http.StatusBadRequest,
			expectedResponse: `{"errors":[{"title":"Bad Request","detail":"You can't purchase your own application","status":"400"}]}`,
		},
		{
			name:             "Should return 409 if already own application",
			input:            map[string]any{"application": 1, "method": 1},
			expectedStatus:   http.StatusConflict,
			expectedResponse: `{"errors":[{"title":"Conflict","detail":"You already own this application","status":"409"}]}`,
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			router, w, req := setupPostOrdersRouter(tc.input, t)
			router.ServeHTTP(w, req)

			if w.Code != tc.expectedStatus {
				t.Errorf("Unexpected status code: got %d want %d", w.Code, tc.expectedStatus)
			}

			response, _ := io.ReadAll(w.Result().Body)
			actualResponse := string(response)
			if actualResponse != tc.expectedResponse {
				t.Errorf("Unexpected body: got %v want %v", actualResponse, tc.expectedResponse)
			}
		})
	}
}

func TestShouldCorrectlyCreateOrder(t *testing.T) {
	messaging.InitMockPubSub()
	database.InitDb(database.InitMockDb())
	router, w, req := setupPostOrdersRouter(map[string]any{"application": 12, "method": 1}, t)
	router.ServeHTTP(w, req)
	if w.Code != http.StatusOK {
		t.Errorf("Unexpected status code: got %d want %d", w.Code, http.StatusOK)
	}
	orders, _ := database.Db.GetOrdersByApplication(12)
	order := orders[0]
	if order.Application.Id != 12 || order.User.Id != 3 || order.Method != 1 {
		t.Errorf("Unexpected order data: got %v", order)
	}
}

func TestShouldForwardErrorFromUserService(t *testing.T) {
	messaging.InitMockPubSub()
	database.InitDb(database.InitMockDb())
	router, w, req := setupPostOrdersRouter(map[string]any{"application": 13, "method": 1}, t)
	router.ServeHTTP(w, req)
	if w.Code != http.StatusBadRequest {
		t.Errorf("Unexpected status code: got %d want %d", w.Code, http.StatusOK)
	}
	response, _ := io.ReadAll(w.Result().Body)
	responseStr := string(response)
	expectedResponse := `{"errors":[{"title":"InsufficientFounds","detail":"You don't have enough founds for this payment","status":"400"}]}`
	if responseStr != expectedResponse {
		t.Errorf("Unexpected response: got %s want %s", responseStr, expectedResponse)
	}
}

func constructJsonReader(in interface{}) (io.Reader, error) {
	buf := bytes.NewBuffer(nil)
	enc := json.NewEncoder(buf)
	err := enc.Encode(in)
	if err != nil {
		return nil, fmt.Errorf("creating reader: error encoding data: %s", err)
	}
	return buf, nil
}

func setupPostOrdersRouter(request map[string]any, t *testing.T) (*gin.Engine, *httptest.ResponseRecorder, *http.Request) {
	user := `{"id":3,"name":"","surname":"", "username": "", "role":"", "balance":10}`
	router := api.InitRouter(api.AppService{AppClient: &testApplicationClient, UserClient: &testUserClient})
	w := httptest.NewRecorder()
	body, err := constructJsonReader(request)
	if err != nil {
		t.Error("Failed to encode test body for creating order")
	}
	req, _ := http.NewRequest("POST", "/v1/orders/", body)
	req.Header.Set("grid-user", user)
	return router, w, req
}

var testApplicationClient = client.ApplicationClient{HttpClient: &TestApplicationHttpClient{}}
var testUserClient = client.UserClient{HttpClient: &TestUserHttpClient{}}

type TestApplicationHttpClient struct {
	client.HttpClient
}

type TestUserHttpClient struct {
	client.HttpClient
}

func (tahc *TestApplicationHttpClient) Get(url string) (resp *http.Response, err error) {
	if url == "http://application-service:8080/internal/3/info?ownership=3" {
		response := `{"owner":3,"price":40.0, "ownership":false}`
		var reader io.Reader = strings.NewReader(response)
		return &http.Response{StatusCode: 200, Body: io.NopCloser(reader)}, nil
	}
	if url == "http://application-service:8080/internal/10/info?ownership=3" {
		response := `{"errors":[{"title":"Not found","status":"404","detail":"Specified application not found !"}]}`
		var reader io.Reader = strings.NewReader(response)
		return &http.Response{StatusCode: 404, Body: io.NopCloser(reader)}, nil
	}
	if url == "http://application-service:8080/internal/1/info?ownership=3" {
		response := `{"owner":1,"price":10.2, "ownership":true}`
		var reader io.Reader = strings.NewReader(response)
		return &http.Response{StatusCode: 200, Body: io.NopCloser(reader)}, nil
	}
	if url == "http://application-service:8080/internal/1/info?ownership=1" {
		response := `{"owner":3,"price":10.2, "ownership":true}`
		var reader io.Reader = strings.NewReader(response)
		return &http.Response{StatusCode: 200, Body: io.NopCloser(reader)}, nil
	}
	if url == "http://application-service:8080/internal/1/info?ownership=4" {
		response := `{"owner":4,"price":10.2, "ownership":true}`
		var reader io.Reader = strings.NewReader(response)
		return &http.Response{StatusCode: 200, Body: io.NopCloser(reader)}, nil
	}
	if url == "http://application-service:8080/internal/12/info?ownership=3" {
		response := `{"owner":4,"price":20, "ownership":false}`
		var reader io.Reader = strings.NewReader(response)
		return &http.Response{StatusCode: 200, Body: io.NopCloser(reader)}, nil
	}
	if url == "http://application-service:8080/internal/13/info?ownership=3" {
		response := `{"owner":4,"price":25, "ownership":false}`
		var reader io.Reader = strings.NewReader(response)
		return &http.Response{StatusCode: 200, Body: io.NopCloser(reader)}, nil
	}
	return nil, nil
}

func (tuhc *TestUserHttpClient) Post(url, contentType string, body io.Reader) (resp *http.Response, err error) {
	buff, _ := io.ReadAll(body)
	buffStr := string(buff)
	if buffStr == `{"payer":3,"payee":4,"amount":25}` {
		reader := strings.NewReader(`{"errors":[{"title":"InsufficientFounds","status":"400","detail":"You don't have enough founds for this payment"}]}`)
		return &http.Response{StatusCode: 400, Body: io.NopCloser(reader)}, nil
	}
	return &http.Response{StatusCode: 200, Body: io.NopCloser(strings.NewReader(""))}, nil
}

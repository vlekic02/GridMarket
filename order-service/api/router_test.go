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
			user: `{"id":3,"name":"","surname":"", "username": "", "role":"", "balance":10}`,
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

func TestShouldReturnCorrectPriceForApplication(t *testing.T) {
	router, w, req := setupPostOrdersRouter(map[string]any{"application": 3, "method": 1, "user": 1}, t)
	router.ServeHTTP(w, req)
	if w.Code != http.StatusOK {
		t.Errorf("Unexpected status code: got %d want %d", w.Code, http.StatusOK)
	}
	expectedResponse := `{"data":{"type":"price","id":"3","attributes":{"price":40}}}`
	responseBody, _ := io.ReadAll(w.Result().Body)
	response := string(responseBody)
	if response != expectedResponse {
		t.Errorf("Unexpected body: got %v want %v", response, expectedResponse)
	}
}

func TestShouldReturn404IfInvalidApplication(t *testing.T) {
	router, w, req := setupPostOrdersRouter(map[string]any{"application": 10, "method": 1, "user": 1}, t)
	router.ServeHTTP(w, req)
	if w.Code != http.StatusNotFound {
		t.Errorf("Unexpected status code: got %d want %d", w.Code, http.StatusNotFound)
	}
	expectedResponse := `{"errors":[{"title":"Not found","detail":"Specified application not found !","status":"404"}]}`
	response, _ := io.ReadAll(w.Result().Body)
	actualResponse := string(response)
	if expectedResponse != actualResponse {
		t.Errorf("Unexpected body: got %v want %v", actualResponse, expectedResponse)
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
	router := api.InitRouter(api.AppService{AppClient: &testApplicationClient})
	w := httptest.NewRecorder()
	body, err := constructJsonReader(request)
	if err != nil {
		t.Error("Failed to encode test body for creating order")
	}
	req, _ := http.NewRequest("POST", "/v1/orders/", body)
	return router, w, req
}

var testApplicationClient = client.ApplicationClient{HttpClient: &TestApplicationHttpClient{}}

type TestApplicationHttpClient struct {
}

func (tahc *TestApplicationHttpClient) Get(url string) (resp *http.Response, err error) {
	if url == "http://application-service:8080/internal/3/price" {
		response := `{"data":{"type":"price","id":"3","attributes":{"price":40.0}}}`
		var reader io.Reader = strings.NewReader(response)
		return &http.Response{StatusCode: 200, Body: io.NopCloser(reader)}, nil
	}
	if url == "http://application-service:8080/internal/10/price" {
		response := `{"errors":[{"title":"Not found","status":"404","detail":"Specified application not found !"}]}`
		var reader io.Reader = strings.NewReader(response)
		return &http.Response{StatusCode: 404, Body: io.NopCloser(reader)}, nil
	}
	if url == "http://application-service:8080/internal/1/owner" {
		response := `{"data":{"type":"user","id":"3"}}`
		var reader io.Reader = strings.NewReader(response)
		return &http.Response{StatusCode: 200, Body: io.NopCloser(reader)}, nil
	}
	return nil, nil
}

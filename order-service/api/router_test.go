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
	"strings"
	"testing"

	"github.com/gin-gonic/gin"
)

var testApplicationClient = client.ApplicationClient{HttpClient: &TestApplicationHttpClient{}}

type TestApplicationHttpClient struct {
}

func (tahc *TestApplicationHttpClient) Get(url string) (resp *http.Response, err error) {
	if url == "http://application-service:8080/internal/3/price" {
		mockResponse := getMockPriceResponse()
		reader, _ := constructJsonReader(mockResponse)
		return &http.Response{StatusCode: 200, Body: io.NopCloser(reader)}, nil
	}
	if url == "http://application-service:8080/internal/10/price" {
		response := `{"errors":[{"type":"error","id":"10","attributes":{"detail":"Specified application not found !","status":404,"title":"Not found"}}]}`
		var reader io.Reader = strings.NewReader(response)
		return &http.Response{StatusCode: 404, Body: io.NopCloser(reader)}, nil
	}
	return nil, nil
}

func TestGetAllOrdersRoute(t *testing.T) {
	router := api.InitRouter(client.DefaultApplicationClient)
	w := httptest.NewRecorder()
	req, _ := http.NewRequest("GET", "/v1/orders/", nil)
	router.ServeHTTP(w, req)
	if w.Code != http.StatusOK {
		t.Errorf("Unexpected status code: got %d want %d", w.Code, http.StatusOK)
	}
	expectedResponse := "Hello !"
	body, _ := io.ReadAll(w.Result().Body)
	response := string(body)
	if strings.TrimSpace(string(response)) != expectedResponse {
		t.Errorf("Unexpected body: got %v want %v", response, expectedResponse)
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
	expectedResponse := `{"data":{"id":"3","type":"price","attributes":{"price":40}}}`
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

func getMockPriceResponse() map[string]any {
	return map[string]any{
		"data": map[string]any{
			"id":   "3",
			"type": "price",
			"attributes": map[string]any{
				"price": 40,
			},
		},
	}
}

func setupPostOrdersRouter(request map[string]any, t *testing.T) (*gin.Engine, *httptest.ResponseRecorder, *http.Request) {
	router := api.InitRouter(testApplicationClient)
	w := httptest.NewRecorder()
	body, err := constructJsonReader(request)
	if err != nil {
		t.Error("Failed to encode test body for creating order")
	}
	req, _ := http.NewRequest("POST", "/v1/orders/", body)
	return router, w, req
}

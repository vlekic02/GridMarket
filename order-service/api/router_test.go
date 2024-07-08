package api_test

import (
	"io"
	"net/http"
	"net/http/httptest"
	"order-service/api"
	"strings"
	"testing"
)

func TestGetAllOrdersRoute(t *testing.T) {
	router := api.InitRouter()
	w := httptest.NewRecorder()
	req, _ := http.NewRequest("GET", "/v1/orders/", nil)
	router.ServeHTTP(w, req)
	if w.Code != 200 {
		t.Errorf("Unexpected status code: got %d want %d", w.Code, http.StatusOK)
	}
	expectedResponse := "Hello !"
	body, _ := io.ReadAll(w.Result().Body)
	response := string(body)
	if strings.TrimSpace(string(response)) != expectedResponse {
		t.Errorf("Unexpected body: got %v want %v", response, expectedResponse)
	}

}

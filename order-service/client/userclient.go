package client

import (
	"bytes"
	"encoding/json"
	"fmt"
	"net/http"
	log "order-service/logging"
	"order-service/model"
)

type UserClient struct {
	HttpClient
}

type UserPayRequest struct {
	Amount float64 `json:"amount"`
}

var DefaultUserClient = UserClient{HttpClient: http.DefaultClient}

func (uc *UserClient) RemoveUserBalance(id int32, request UserPayRequest) error {
	log.Debug(fmt.Sprintf("Calling user service /internal/users/%d/pay", id))
	buf, err := json.Marshal(&request)
	if err != nil {
		log.Error("Failed to marshal user pay request !", "error", err)
		return model.NewRestError(500, "Internal Server Error", "Failed to marshal user service response ! Error: "+err.Error())
	}
	response, err := uc.Post(fmt.Sprintf("http://user-service:8080/internal/users/%d/pay", id), "application/json", bytes.NewReader(buf))
	if err != nil {
		return model.NewRestError(504, "Gateway Timeout", "Application service did not respond ! Error: "+err.Error())
	}
	defer response.Body.Close()
	log.Debug("a", response.StatusCode)
	if response.StatusCode == http.StatusOK {
		return nil
	}
	errorResponse, err := UnmarshalErrors(response.Body)
	if err != nil {
		return model.NewRestError(500, "Internal Server Error", "Failed to unmarshal user service response ! Error: "+err.Error())
	}
	return errorResponse
}

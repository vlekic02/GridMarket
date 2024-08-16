package client

import (
	"bytes"
	"encoding/json"
	"net/http"
	log "order-service/logging"
	"order-service/model"
)

type UserClient struct {
	HttpClient
}

type UserTransactionRequest struct {
	Payer  int32   `json:"payer"`
	Payee  int32   `json:"payee"`
	Amount float64 `json:"amount"`
}

var DefaultUserClient = UserClient{HttpClient: http.DefaultClient}

func (uc *UserClient) MakeTransaction(request UserTransactionRequest) error {
	log.Debug("Calling /internal/users/transaction with transaction data", "data", request)
	buf, err := json.Marshal(&request)
	if err != nil {
		log.Error("Failed to marshal user pay request !", "error", err)
		return model.NewRestError(500, "Internal Server Error", "Failed to marshal user service response ! Error: "+err.Error())
	}
	response, err := uc.Post("http://user-service:8080/internal/users/transaction", "application/json", bytes.NewReader(buf))
	if err != nil {
		return model.NewRestError(504, "Gateway Timeout", "Application service did not respond ! Error: "+err.Error())
	}
	defer response.Body.Close()
	if response.StatusCode == http.StatusOK {
		return nil
	}
	errorResponse, err := UnmarshalErrors(response.Body)
	if err != nil {
		return model.NewRestError(500, "Internal Server Error", "Failed to unmarshal user service response ! Error: "+err.Error())
	}
	return errorResponse
}

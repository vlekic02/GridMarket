package client

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	log "order-service/logging"
	"order-service/model"
)

var DefaultApplicationClient = ApplicationClient{HttpClient: http.DefaultClient}

type ApplicationClient struct {
	HttpClient
}

type ApplicationPriceResponse struct {
	Data struct {
		Id         string `json:"id"`
		Type       string `json:"type"`
		Attributes struct {
			Price float64 `json:"price"`
		} `json:"attributes"`
	} `json:"data"`
}

func (app *ApplicationClient) GetApplicationPrice(id uint64) (ApplicationPriceResponse, error) {
	log.Debug(fmt.Sprintf("Calling application service /internal/%d/price", id))
	response, err := app.Get(fmt.Sprintf("http://application-service:8080/internal/%d/price", id))
	applicationResponse := ApplicationPriceResponse{}
	if err != nil {
		return applicationResponse, model.NewRestError(504, "Gateway Timeout", "Application service did not respond ! Error: "+err.Error())
	}
	defer response.Body.Close()
	body, err := io.ReadAll(response.Body)
	if err != nil {
		return applicationResponse, model.NewRestError(500, "Internal Server Error", "Application service response does not contains body ! Error: "+err.Error())
	}
	if response.StatusCode != http.StatusOK {
		errorResponse := new(model.ErrorsResponse)
		if err := json.Unmarshal(body, &errorResponse); err != nil {
			return applicationResponse, model.NewRestError(500, "Internal Server Error", "Failed to unmarshal application service response ! Error: "+err.Error())
		}
		return applicationResponse, &errorResponse.Errors[0].Attributes
	}
	if err := json.Unmarshal(body, &applicationResponse); err != nil {
		return applicationResponse, model.NewRestError(500, "Internal Server Error", "Failed to unmarshal application service response ! Error: "+err.Error())
	}
	return applicationResponse, nil
}

package client

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	log "order-service/logging"
	"order-service/model"

	"github.com/google/jsonapi"
)

var DefaultApplicationClient = ApplicationClient{HttpClient: http.DefaultClient}

type ApplicationClient struct {
	HttpClient
}

type ApplicationPriceResponse struct {
	Id    string  `jsonapi:"primary,price"`
	Price float64 `jsonapi:"attr,price"`
}

type ApplicationOwnerResponse struct {
	Id string `jsonapi:"primary,user"`
}

func (app *ApplicationClient) GetApplicationPrice(id int32) (ApplicationPriceResponse, error) {
	log.Debug(fmt.Sprintf("Calling application service /internal/%d/price", id))
	response, err := app.Get(fmt.Sprintf("http://application-service:8080/internal/%d/price", id))
	applicationResponse := ApplicationPriceResponse{}
	if err != nil {
		return applicationResponse, model.NewRestError(504, "Gateway Timeout", "Application service did not respond ! Error: "+err.Error())
	}
	defer response.Body.Close()
	if response.StatusCode != http.StatusOK {
		errorResponse, err := UnmarshalErrors(response.Body)
		if err != nil {
			return applicationResponse, model.NewRestError(500, "Internal Server Error", "Failed to unmarshal application service response ! Error: "+err.Error())
		}
		return applicationResponse, errorResponse
	}
	if err := jsonapi.UnmarshalPayload(response.Body, &applicationResponse); err != nil {
		return applicationResponse, model.NewRestError(500, "Internal Server Error", "Failed to unmarshal application service response ! Error: "+err.Error())
	}
	return applicationResponse, nil
}

func (app *ApplicationClient) GetApplicationOwner(id int32) (ApplicationOwnerResponse, *model.ErrorResponse) {
	log.Debug(fmt.Sprintf("Calling application service /internal/%d/owner", id))
	response, err := app.Get(fmt.Sprintf("http://application-service:8080/internal/%d/owner", id))
	applicationResponse := ApplicationOwnerResponse{}
	if err != nil {
		return applicationResponse, model.NewRestError(504, "Gateway Timeout", "Application service did not respond ! Error: "+err.Error())
	}
	defer response.Body.Close()
	if response.StatusCode != http.StatusOK {
		errorResponse, err := UnmarshalErrors(response.Body)
		if err != nil {
			return applicationResponse, model.NewRestError(500, "Internal Server Error", "Failed to unmarshal application service response ! Error: "+err.Error())
		}
		return applicationResponse, errorResponse
	}
	if err := jsonapi.UnmarshalPayload(response.Body, &applicationResponse); err != nil {
		return applicationResponse, model.NewRestError(500, "Internal Server Error", "Failed to unmarshal application service response ! Error: "+err.Error())
	}
	return applicationResponse, nil
}

func UnmarshalErrors(in io.Reader) (*model.ErrorResponse, error) {
	payload := new(model.ErrorResponse)

	if err := json.NewDecoder(in).Decode(payload); err != nil {
		return nil, err
	}

	return payload, nil
}

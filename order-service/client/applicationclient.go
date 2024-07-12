package client

import (
	"fmt"
	"net/http"
	"order-service/logging"
	log "order-service/logging"
	"order-service/model"

	"github.com/google/jsonapi"
)

var DefaultApplicationClient = ApplicationClient{HttpClient: http.DefaultClient}

type ApplicationClient struct {
	HttpClient
}

type ApplicationPriceResponse struct {
	ID    string  `jsonapi:"primary,price"`
	Price float64 `jsonapi:"attr,price"`
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
		errorResponse := new(model.ErrorResponse)
		if err := jsonapi.UnmarshalPayload(response.Body, errorResponse); err != nil {
			return applicationResponse, model.NewRestError(500, "Internal Server Error", "Failed to unmarshal application service response ! Error: "+err.Error())
		}
		logging.Info("a", errorResponse)
		return applicationResponse, &errorResponse.Errors[0]
	}
	if err := jsonapi.UnmarshalPayload(response.Body, &applicationResponse); err != nil {
		return applicationResponse, model.NewRestError(500, "Internal Server Error", "Failed to unmarshal application service response ! Error: "+err.Error())
	}
	return applicationResponse, nil
}

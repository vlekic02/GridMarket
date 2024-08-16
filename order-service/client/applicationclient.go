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

type ApplicationInfoResponse struct {
	Price float64 `json:"price"`
	Owner int32   `json:"owner"`
}

func (app *ApplicationClient) GetApplicationInfo(id int32) (ApplicationInfoResponse, error) {
	log.Debug(fmt.Sprintf("Calling application service /internal/%d/info", id))
	response, err := app.Get(fmt.Sprintf("http://application-service:8080/internal/%d/info", id))
	applicationResponse := ApplicationInfoResponse{}
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
	if err := json.NewDecoder(response.Body).Decode(&applicationResponse); err != nil {
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

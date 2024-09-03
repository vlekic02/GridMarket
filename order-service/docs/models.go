package docs

import (
	"order-service/model"
	"time"
)

type Order struct {
	Id            int32           `json:"id"`
	Type          string          `json:"type" example:"order"`
	Attributes    OrderAttributes `json:"attributes"`
	Relationships OrderRelations  `json:"relationships"`
}

type OrderAttributes struct {
	Date   time.Time           `json:"date"`
	Method model.PaymentMethod `json:"method"`
}

type OrderRelations struct {
	User        model.User        `json:"user"`
	Application model.Application `json:"application"`
}

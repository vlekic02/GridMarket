package model

import (
	"fmt"
	"time"
)

type PaymentMethod int

const (
	Balance = iota
	Paypal
)

var methodNames = map[PaymentMethod]string{
	Balance: "Balance",
	Paypal:  "Paypal",
}

func (pm PaymentMethod) String() string {
	return methodNames[pm]
}

type Order struct {
	ID          uint64
	User        uint64
	Application uint64
	Date        time.Time
	Method      PaymentMethod
}

type OrderRequest struct {
	User        uint64        `json:"user" binding:"required"`
	Application uint64        `json:"application" binding:"required"`
	Method      PaymentMethod `json:"method" binding:"required,oneof=0 1"`
}

type RestError struct {
	Title  string `json:"title"`
	Status uint16 `json:"status"`
	Detail string `json:"detail"`
}

func (re *RestError) Error() string {
	return fmt.Sprintf("Status: %d; Title: %s; Detail: %s", re.Status, re.Title, re.Detail)
}

func NewRestError(status uint16, title string, detail string) *RestError {
	return &RestError{Title: title, Status: status, Detail: detail}
}

type ErrorItem struct {
	Type       string    `json:"type"`
	ID         string    `json:"id"`
	Attributes RestError `json:"attributes"`
}

type ErrorsResponse struct {
	Errors []ErrorItem `json:"errors"`
}

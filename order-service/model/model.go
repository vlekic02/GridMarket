package model

import (
	"fmt"
	"time"
)

type PaymentMethod int

const (
	Balance PaymentMethod = iota
	Paypal
)

var methodNames = map[PaymentMethod]string{
	Balance: "BALANCE",
	Paypal:  "PAYPAL",
}

var reverseMethodMapping = map[string]PaymentMethod{
	"BALANCE": Balance,
	"PAYPAL":  Paypal,
}

func (pm PaymentMethod) String() string {
	return methodNames[pm]
}

func GetPaymentMethodByName(name string) PaymentMethod {
	return reverseMethodMapping[name]
}

type Order struct {
	ID          int32         `jsonapi:"primary,order"`
	User        int32         `jsonapi:"relation,user"`
	Application int32         `jsonapi:"relation,application"`
	Date        time.Time     `jsonapi:"attr,date"`
	Method      PaymentMethod `jsonapi:"attr,method"`
}

type OrderRequest struct {
	User        int32         `json:"user" binding:"required"`
	Application int32         `json:"application" binding:"required"`
	Method      PaymentMethod `json:"method" binding:"required,oneof=0 1"`
}

type RestError struct {
	Id     string `jsonapi:"primary,error"`
	Title  string `jsonapi:"attr,title"`
	Status uint16 `jsonapi:"attr,status"`
	Detail string `jsonapi:"attr,detail"`
}

type ErrorResponse struct {
	Errors []RestError
}

func (re *RestError) Error() string {
	return fmt.Sprintf("Status: %d; Title: %s; Detail: %s", re.Status, re.Title, re.Detail)
}

func NewRestError(status uint16, title string, detail string) *RestError {
	return &RestError{Title: title, Status: status, Detail: detail}
}

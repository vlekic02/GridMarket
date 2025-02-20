package model

import (
	"fmt"
	"strconv"
	"strings"
	"time"

	"github.com/google/jsonapi"
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
	Id          int32         `jsonapi:"primary,order"`
	User        *User         `jsonapi:"relation,user"`
	Application *Application  `jsonapi:"relation,application"`
	Date        time.Time     `jsonapi:"attr,date"`
	Method      PaymentMethod `jsonapi:"attr,method"`
}

type User struct {
	Id int32 `jsonapi:"primary,user"`
}

type Application struct {
	Id int32 `jsonapi:"primary,application"`
}

type OrderRequest struct {
	User        int32          `json:"user"`
	Application int32          `json:"application" binding:"required"`
	Method      *PaymentMethod `json:"method" binding:"required,oneof=0 1"`
}

type UserInfo struct {
	Id       int32   `json:"id"`
	Name     string  `json:"name"`
	Surname  string  `json:"surname"`
	Username string  `json:"username"`
	Role     string  `json:"role"`
	Balance  float64 `json:"balance"`
}

func (ui *UserInfo) IsAdmin() bool {
	return ui.Role == "ADMIN"
}

func NewRestError(status int, title string, detail string) *ErrorResponse {
	return &ErrorResponse{[]*jsonapi.ErrorObject{{Title: title, Status: strconv.Itoa(status), Detail: detail}}}
}

type ErrorResponse jsonapi.ErrorsPayload

func (er *ErrorResponse) Error() string {
	sb := strings.Builder{}
	sb.WriteString("Errors: [")
	for _, err := range er.Errors {
		sb.WriteString(fmt.Sprintf("(Title: %s; Status: %s; Detail: %s;)", err.Title, err.Status, err.Detail))
	}
	sb.WriteString("]")
	return sb.String()
}

package model

import (
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

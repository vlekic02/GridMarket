package model

import "time"

type Order struct {
	ID          uint64
	User        uint64
	Application uint64
	Date        time.Time
	Method      PaymentMethod
}

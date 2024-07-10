package database

import "order-service/model"

type Database interface {
	GetAllOrders() ([]model.Order, error)
	InsertOrder(model.OrderRequest) error
	Close()
}

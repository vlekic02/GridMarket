package database

import "order-service/model"

type Database interface {
	GetAllOrders() ([]model.Order, error)
	InsertOrder(or model.OrderRequest) error
	Close()
}

var Db Database

func InitDb(database Database) {
	Db = database
}

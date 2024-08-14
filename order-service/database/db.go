package database

import "order-service/model"

type Database interface {
	GetAllOrders() ([]model.Order, error)
	InsertOrder(or model.OrderRequest) error
	GetOrdersByUser(userId int32) ([]model.Order, error)
	GetOrdersByApplication(applicationId int32) ([]model.Order, error)
	Close()
}

var Db Database

func InitDb(database Database) {
	Db = database
}

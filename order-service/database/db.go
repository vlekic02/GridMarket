package database

import (
	"context"
	"order-service/model"

	"github.com/jackc/pgx/v5"
)

type Database interface {
	InsertOrder(or model.OrderRequest, ctx context.Context, tx pgx.Tx) error
	ExecTransaction(ctx context.Context, fn func(ctx context.Context, tx pgx.Tx) error) (err error)
	GetOrdersByUser(userId int32) ([]*model.Order, error)
	GetOrdersByApplication(applicationId int32) ([]*model.Order, error)
	Close()
}

var Db Database

func InitDb(database Database) {
	Db = database
}

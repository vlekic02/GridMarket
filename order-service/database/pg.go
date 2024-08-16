package database

import (
	"context"
	"order-service/model"
	"os"
	"time"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

type postgres struct {
	db *pgxpool.Pool
}

func (pg *postgres) GetAllOrders() ([]*model.Order, error) {
	query := `SELECT * FROM grid_order`
	rows, err := pg.db.Query(context.Background(), query)
	if err != nil {
		return nil, err
	}
	orders, err := pgx.CollectRows[*model.Order](rows, rowToOrder)
	if err != nil {
		return nil, err
	}
	return orders, nil
}

func (pg *postgres) InsertOrder(or model.OrderRequest) error {
	query := `INSERT INTO grid_order VALUES (default, @user, @application, @date, @method)`
	args := pgx.NamedArgs{
		"user":        or.User,
		"application": or.Application,
		"date":        time.Now(),
		"method":      or.Method.String(),
	}
	_, err := pg.db.Exec(context.Background(), query, args)
	return err
}

func (pg *postgres) GetOrdersByUser(userId int32) ([]*model.Order, error) {
	query := `SELECT * FROM grid_order WHERE "user" = @user`
	args := pgx.NamedArgs{
		"user": userId,
	}
	rows, err := pg.db.Query(context.Background(), query, args)
	if err != nil {
		return nil, err
	}
	orders, err := pgx.CollectRows[*model.Order](rows, rowToOrder)
	if err != nil {
		return nil, err
	}
	return orders, nil
}

func (pg *postgres) GetOrdersByApplication(applicationId int32) ([]*model.Order, error) {
	query := `SELECT * FROM grid_order WHERE application = @app`
	args := pgx.NamedArgs{
		"app": applicationId,
	}
	rows, err := pg.db.Query(context.Background(), query, args)
	if err != nil {
		return nil, err
	}
	orders, err := pgx.CollectRows[*model.Order](rows, rowToOrder)
	if err != nil {
		return nil, err
	}
	return orders, nil
}

func (pg *postgres) Close() {
	pg.db.Close()
}
func rowToOrder(row pgx.CollectableRow) (*model.Order, error) {
	order := new(model.Order)
	values, err := row.Values()
	if err != nil {
		return nil, err
	}
	order.Id = values[0].(int32)
	order.User = &model.User{Id: values[1].(int32)}
	order.Application = &model.Application{Id: values[2].(int32)}
	order.Date = values[3].(time.Time)
	order.Method = model.GetPaymentMethodByName(values[4].(string))
	return order, nil
}

func InitPgDatabase() (*postgres, error) {
	pgConfig, err := PoolConfig()
	if err != nil {
		return nil, err
	}
	dbPool, err := pgxpool.NewWithConfig(context.Background(), pgConfig)
	if err != nil {
		return nil, err
	}
	return &postgres{db: dbPool}, nil
}

func PoolConfig() (*pgxpool.Config, error) {
	databaseUrl := os.Getenv("DATABASE_URL")

	dbConfig, err := pgxpool.ParseConfig(databaseUrl)
	if err != nil {
		return nil, err
	}
	dbConfig.MaxConns = 10
	dbConfig.MinConns = 0
	dbConfig.ConnConfig.ConnectTimeout = time.Second * 30
	dbConfig.MaxConnLifetime = time.Minute * 30
	dbConfig.MaxConnIdleTime = time.Minute * 15

	return dbConfig, nil
}

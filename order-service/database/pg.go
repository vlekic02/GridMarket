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

func (pg *postgres) GetAllOrders() ([]model.Order, error) {
	query := `SELECT * FROM "order"`
	rows, err := pg.db.Query(context.Background(), query)
	if err != nil {
		return nil, err
	}
	orders, err := pgx.CollectRows[model.Order](rows, pgx.RowToStructByPos[model.Order])
	if err != nil {
		return nil, err
	}
	return orders, nil
}

func (pg *postgres) InsertOrder(or model.OrderRequest) error {
	query := `INSERT INTO "order" VALUES (default, @user, @application, @date, @method)`
	args := pgx.NamedArgs{
		"user":        or.User,
		"application": or.Application,
		"date":        time.Now(),
		"method":      or.Method,
	}
	_, err := pg.db.Exec(context.Background(), query, args)
	return err
}

func (pg *postgres) Close() {
	pg.db.Close()
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

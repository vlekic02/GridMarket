package database

import (
	"context"
	"order-service/model"
	"os"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

type postgres struct {
	db *pgxpool.Pool
}

var PgInstance Database

func (pg *postgres) GetAllOrders() ([]model.Order, error) {
	return []model.Order{}, nil
}

func (pg *postgres) InsertOrder(model.OrderRequest) error {
	return nil
}

func (pg *postgres) Close() {
	pg.db.Close()
}

func InitPgDatabase() error {
	pgConfig, err := PoolConfig()
	if err != nil {
		return err
	}
	dbPool, err := pgxpool.NewWithConfig(context.Background(), pgConfig)
	if err != nil {
		return err
	}
	PgInstance = &postgres{db: dbPool}
	return nil
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

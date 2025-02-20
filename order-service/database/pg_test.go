package database_test

import (
	"context"
	"order-service/database"
	"order-service/model"
	"os"
	"testing"
	"time"

	"github.com/jackc/pgx/v5"
	"github.com/testcontainers/testcontainers-go"
	"github.com/testcontainers/testcontainers-go/modules/postgres"
	"github.com/testcontainers/testcontainers-go/wait"
)

func setupContainer() *postgres.PostgresContainer {
	pgContainer, _ := postgres.Run(
		context.Background(),
		"postgres:16-alpine",
		postgres.WithUsername("test"),
		postgres.WithPassword("test"),
		postgres.WithDatabase("testdb"),
		postgres.WithInitScripts("../schema_test.sql"),
		testcontainers.WithWaitStrategy(
			wait.ForLog("database system is ready to accept connections").
				WithOccurrence(2).
				WithStartupTimeout(5*time.Second)),
	)
	return pgContainer
}

func TestMain(m *testing.M) {
	pgCon := setupContainer()
	connStr, _ := pgCon.ConnectionString(context.Background())
	os.Setenv("DATABASE_URL", connStr)
	pg, _ := database.InitPgDatabase()
	database.InitDb(pg)
	code := m.Run()
	database.Db.Close()
	pgCon.Terminate(context.Background())
	os.Unsetenv("DATABASE_URL")
	os.Exit(code)
}

func TestGetOrdersByApplication(t *testing.T) {
	orders, _ := database.Db.GetOrdersByApplication(2)
	expected := 2
	actual := len(orders)
	if actual != expected {
		t.Errorf("Unexpected length of order slice ! got: %d want: %d", actual, expected)
	}
	for _, o := range orders {
		if o.Application.Id != 2 {
			t.Errorf("Unexpected application id ! got: %d want: %d", o.Application.Id, 2)
		}
	}
}

func TestInsertOrder(t *testing.T) {
	balance := model.Balance
	orderRequest := model.OrderRequest{User: 10, Application: 10, Method: &balance}
	database.Db.ExecTransaction(context.Background(), func(ctx context.Context, tx pgx.Tx) error {
		return database.Db.InsertOrder(orderRequest, ctx, tx)
	})
	byUser, _ := database.Db.GetOrdersByUser(10)
	actual := len(byUser)
	expected := 1
	if actual != expected {
		t.Errorf("Unexpected length of order slice ! got: %d want: %d", actual, expected)
	}
}

func TestGeyByUser(t *testing.T) {
	orders, _ := database.Db.GetOrdersByUser(5)
	order := orders[0]
	if order.Id != 4 || order.User.Id != 5 || order.Application.Id != 5 || order.Method != model.Paypal {
		t.Errorf("Unexpected struct returned ! got: %v", order)
	}
}

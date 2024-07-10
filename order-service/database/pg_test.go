package database_test

import (
	"context"
	"order-service/database"
	"order-service/logging"
	"order-service/model"
	"os"
	"testing"
	"time"

	"github.com/testcontainers/testcontainers-go"
	"github.com/testcontainers/testcontainers-go/modules/postgres"
	"github.com/testcontainers/testcontainers-go/wait"
)

var pg database.Database

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
	pg, _ = database.InitPgDatabase()
	code := m.Run()
	pg.Close()
	pgCon.Terminate(context.Background())
	os.Unsetenv("DATABASE_URL")
	os.Exit(code)
}

func TestGetAllOrders(t *testing.T) {
	orders, err := pg.GetAllOrders()
	if err != nil {
		logging.Info("", err)
	}
	expected := 4
	actual := len(orders)
	if actual != expected {
		t.Errorf("Unexpected length of order slice ! got: %d want: %d", actual, expected)
	}
}

func TestInsertOrder(t *testing.T) {
	orderRequest := model.OrderRequest{User: 10, Application: 10, Method: model.Balance}
	pg.InsertOrder(orderRequest)
	byUser, _ := pg.GetOrdersByUser(10)
	actual := len(byUser)
	expected := 1
	if actual != expected {
		t.Errorf("Unexpected length of order slice ! got: %d want: %d", actual, expected)
	}
}

func TestGeyByUser(t *testing.T) {
	orders, _ := pg.GetOrdersByUser(1)
	order := orders[0]
	if order.ID != 1 && order.User != 2 && order.Application != 2 && order.Method != model.Balance {
		t.Errorf("Unexpected struct returned ! got: %v", order)
	}
}

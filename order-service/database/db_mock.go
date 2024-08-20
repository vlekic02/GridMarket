package database

import (
	"context"
	"order-service/model"
	"time"

	"github.com/jackc/pgx/v5"
)

type mockdb struct {
	lastId int32
	orders []*model.Order
}

func InitMockDb() *mockdb {
	time, _ := time.Parse(time.DateOnly, "2024-01-02")
	orders := []*model.Order{
		{Id: 1, User: &model.User{Id: 1}, Application: &model.Application{Id: 1}, Date: time, Method: 1},
		{Id: 2, User: &model.User{Id: 1}, Application: &model.Application{Id: 3}, Date: time, Method: 1},
		{Id: 3, User: &model.User{Id: 2}, Application: &model.Application{Id: 2}, Date: time, Method: 1},
		{Id: 4, User: &model.User{Id: 3}, Application: &model.Application{Id: 1}, Date: time, Method: 1},
	}

	return &mockdb{lastId: 5, orders: orders}
}

func (m *mockdb) InsertOrder(or model.OrderRequest, ctx context.Context, tx pgx.Tx) error {
	last := m.lastId
	m.lastId = m.lastId + 1
	m.orders = append(m.orders, &model.Order{Id: last, User: &model.User{Id: or.User}, Application: &model.Application{Id: or.Application}, Date: time.Now(), Method: *or.Method})
	return nil
}
func (m *mockdb) GetOrdersByUser(userId int32) ([]*model.Order, error) {
	result := []*model.Order{}
	for _, order := range m.orders {
		if order.User.Id == userId {
			result = append(result, order)
		}
	}
	return result, nil
}
func (m *mockdb) GetOrdersByApplication(applicationId int32) ([]*model.Order, error) {
	result := []*model.Order{}
	for _, order := range m.orders {
		if order.Application.Id == applicationId {
			result = append(result, order)
		}
	}
	return result, nil
}

func (m *mockdb) Close() {

}

func (m *mockdb) ExecTransaction(ctx context.Context, fn func(ctx context.Context, tx pgx.Tx) error) (err error) {
	return fn(ctx, nil)
}

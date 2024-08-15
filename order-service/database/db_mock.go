package database

import (
	"order-service/model"
	"time"
)

type mockdb struct {
	lastId int32
	orders []model.Order
}

func InitMockDb() *mockdb {
	time, _ := time.Parse(time.DateOnly, "2024-01-02")
	orders := []model.Order{
		{ID: 1, User: 1, Application: 1, Date: time, Method: 1},
		{ID: 2, User: 1, Application: 3, Date: time, Method: 1},
		{ID: 3, User: 2, Application: 2, Date: time, Method: 1},
		{ID: 4, User: 3, Application: 1, Date: time, Method: 1},
	}

	return &mockdb{lastId: 5, orders: orders}
}

func (m *mockdb) GetAllOrders() ([]model.Order, error) {
	return m.orders, nil
}
func (m *mockdb) InsertOrder(or model.OrderRequest) error {
	last := m.lastId
	m.lastId = m.lastId + 1
	m.orders = append(m.orders, model.Order{ID: last, User: or.User, Application: or.Application, Date: time.Now(), Method: *or.Method})
	return nil
}
func (m *mockdb) GetOrdersByUser(userId int32) ([]model.Order, error) {
	result := []model.Order{}
	for _, order := range m.orders {
		if order.User == userId {
			result = append(result, order)
		}
	}
	return result, nil
}
func (m *mockdb) GetOrdersByApplication(applicationId int32) ([]model.Order, error) {
	result := []model.Order{}
	for _, order := range m.orders {
		if order.Application == applicationId {
			result = append(result, order)
		}
	}
	return result, nil
}

func (m *mockdb) Close() {

}

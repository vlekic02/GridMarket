package api

import (
	"order-service/client"
	"order-service/database"
	log "order-service/logging"
	"order-service/model"

	"github.com/gin-gonic/gin"
	"github.com/google/jsonapi"
)

type AppService struct {
	AppClient  *client.ApplicationClient
	UserClient *client.UserClient
}

// @Summary	Returns orders
// @Tags		Order
// @Produce	application/vnd.api+json
// @Param		user		query		int	false	"User id"
// @Param		application	query		int	false	"Application id"
// @Success	200			{object}	docs.Order
// @Router		/v1/orders/ [get]
func GetAllOrders() gin.HandlerFunc {
	return func(ctx *gin.Context) {
		userId, ok := ctx.Keys["userId"]
		if ok {
			fetchOrderAndRespond(database.Db.GetOrdersByUser, userId.(int), ctx)
			return
		}
		applicationId, ok := ctx.Keys["applicationId"]
		if ok {
			fetchOrderAndRespond(database.Db.GetOrdersByApplication, applicationId.(int), ctx)
		}
	}
}

func fetchOrderAndRespond(fetch func(int32) ([]*model.Order, error), id int, ctx *gin.Context) {
	orders, err := fetch(int32(id))
	if err != nil {
		log.Error("Error while fetching orders", "error", err)
		ctx.Error(err)
	}
	payload, _ := jsonapi.Marshal(orders)
	ctx.JSON(200, payload)
}

// @Summary	Create a new order
// @Tags		Order
// @Accept		application/json
// @Produce	application/vnd.api+json
// @Param		orderRequest	body	model.OrderRequest	true	"Order request"
// @Success	201
// @Failure	400	{object}	model.ErrorResponse
// @Failure	404	{object}	model.ErrorResponse
// @Failure	504	{object}	model.ErrorResponse
// @Failure	500	{object}	model.ErrorResponse
// @Router		/v1/orders/ [post]
func (service *AppService) CreateOrder() gin.HandlerFunc {
	return func(ctx *gin.Context) {
		request, _ := ctx.Get("orderRequest")
		orderRequest := request.(*model.OrderRequest)
		applicationPrice, err := service.AppClient.GetApplicationPrice(orderRequest.Application)
		if err != nil {
			log.Error("Error while fetching application service", "error", err)
			ctx.Error(err)
			return
		}
		err = service.UserClient.RemoveUserBalance(orderRequest.User, client.UserPayRequest{Amount: applicationPrice.Price})
		if err != nil {
			ctx.Error(err)
			return
		}
		ctx.Status(200)
	}
}

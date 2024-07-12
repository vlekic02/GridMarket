package order

import (
	"order-service/client"
	log "order-service/logging"
	"order-service/model"

	"github.com/gin-gonic/gin"
	"github.com/google/jsonapi"
)

// @Summary	Returns hello
// @Tags		Order
// @Produce	plain
// @Success	200
// @Router		/v1/orders/ [get]
func GetAllOrders(ctx *gin.Context) {
	ctx.String(200, "Hello !")
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
func CreateOrder(app client.ApplicationClient) gin.HandlerFunc {
	return func(ctx *gin.Context) {
		request, _ := ctx.Get("orderRequest")
		orderRequest := request.(*model.OrderRequest)
		applicationPrice, err := app.GetApplicationPrice(orderRequest.Application)
		if err != nil {
			log.Error("Error while fetching application service", "error", err)
			ctx.Error(err)
			return
		}
		payload, _ := jsonapi.Marshal(&applicationPrice)
		ctx.JSON(200, payload)
	}
}

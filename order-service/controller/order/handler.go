package order

import (
	"fmt"
	"order-service/client"
	log "order-service/logging"
	"order-service/model"

	"github.com/gin-gonic/gin"
)

// @Summary	Returns hello
// @Tags Order
// @Produce	plain
// @Success	200
// @Router		/v1/orders/ [get]
func GetAllOrders(ctx *gin.Context) {
	ctx.String(200, "Hello !")
}

// @Summary Create a new order
// @Tags Order
// @Accept application/json
// @Produce application/json
// @Success 200 {object} model.OrderRequest
// @Failure 400 {object} model.RestError
// @Failure 404 {object} model.RestError
// @Failure 504 {object} model.RestError
// @Failure 500 {object} model.RestError
// @Router /v1/orders/ [post]
func CreateOrder(ctx *gin.Context) {
	request, _ := ctx.Get("orderRequest")
	orderRequest := request.(*model.OrderRequest)
	applicationPrice, err := client.GetApplicationPrice(orderRequest.Application)
	if err != nil {
		log.Error("Error while fetching application service", err)
		ctx.Error(err)
		return
	}
	ctx.String(200, fmt.Sprintf("%v", applicationPrice))
}

package order

import (
	"fmt"

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
// @Failure 400 {string} string "Bad Request"
// @Router /v1/orders/ [post]
func CreateOrder(ctx *gin.Context) {
	orderRequest, _ := ctx.Get("body")
	ctx.String(200, fmt.Sprintf("%v", orderRequest))
}

package order

import "github.com/gin-gonic/gin"

// @Summary	Returns hello
// @Produce	plain
// @Success	200
// @Router		/v1/orders/ [get]
func GetAllOrders(context *gin.Context) {
	context.String(200, "Hello !")
}

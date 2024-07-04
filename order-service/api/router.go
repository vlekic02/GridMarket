package api

import (
	"order-service/controller/order"

	"github.com/gin-gonic/gin"
)

func InitRouter() *gin.Engine {
	app := gin.Default()

	v1 := app.Group("v1/order")
	{
		v1.GET("/", order.Get)
	}
	return app
}

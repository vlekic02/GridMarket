package api

import (
	"order-service/controller/order"

	"order-service/docs"

	"github.com/gin-gonic/gin"

	swaggerFiles "github.com/swaggo/files"

	ginSwagger "github.com/swaggo/gin-swagger"
)

func InitRouter() *gin.Engine {
	app := gin.Default()

	docs.SwaggerInfo.BasePath = "/v1/orders"
	v1 := app.Group("v1/orders")
	{
		v1.GET("/", order.Get)
	}
	app.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))
	return app
}

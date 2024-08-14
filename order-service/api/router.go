package api

import (
	"log/slog"

	"order-service/client"
	"order-service/controller/order"

	"order-service/logging"

	"order-service/docs"

	"github.com/gin-gonic/gin"

	swaggerFiles "github.com/swaggo/files"

	ginSwagger "github.com/swaggo/gin-swagger"
)

func InitRouter(cApp client.ApplicationClient) *gin.Engine {
	app := gin.New()

	app.Use(gin.Recovery(), ErrorHandler(), JsonLogger())

	if gin.Mode() == "debug" {
		logging.SetLevel(slog.LevelDebug)
	}

	docs.SwaggerInfo.BasePath = "/v1/orders"
	v1 := app.Group("v1/orders", ExtractUserInfo())
	{
		v1.GET("/", ValidateGetOrdersQuery(cApp), order.GetAllOrders)
		v1.POST("/", ValidateOrder(), order.CreateOrder(cApp))
	}
	app.GET("/swagger-ui/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))
	return app
}

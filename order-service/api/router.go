package api

import (
	"log/slog"

	"order-service/controller/order"

	"order-service/logging"

	"order-service/docs"

	"github.com/gin-gonic/gin"

	swaggerFiles "github.com/swaggo/files"

	ginSwagger "github.com/swaggo/gin-swagger"
)

func InitRouter() *gin.Engine {
	app := gin.New()

	app.Use(gin.Recovery())
	app.Use(JsonLogger())

	if gin.Mode() == "debug" {
		logging.SetLevel(slog.LevelDebug)
	}

	docs.SwaggerInfo.BasePath = "/v1/orders"
	v1 := app.Group("v1/orders")
	{
		v1.GET("/", order.Get)
	}
	app.GET("/swagger-ui/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))
	return app
}

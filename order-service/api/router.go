package api

import (
	"log/slog"

	"order-service/logging"

	"order-service/docs"

	"github.com/gin-gonic/gin"
	"github.com/penglongli/gin-metrics/ginmetrics"

	swaggerFiles "github.com/swaggo/files"

	ginSwagger "github.com/swaggo/gin-swagger"
)

func InitRouter(service AppService) *gin.Engine {
	app := gin.New()

	m := ginmetrics.GetMonitor()
	m.SetMetricPath("/metrics")
	m.Use(app)

	app.Use(gin.Recovery(), ErrorHandler(), JsonLogger(), JsonApiMiddleware())

	if gin.Mode() == "debug" {
		logging.SetLevel(slog.LevelDebug)
	}

	docs.SwaggerInfo.BasePath = "/v1/orders"
	v1 := app.Group("v1/orders", ExtractUserInfo())
	{
		v1.GET("/", service.ValidateGetOrdersQuery(), GetAllOrders())
		v1.POST("/", ValidateOrder(), service.CreateOrder())
	}
	app.GET("/swagger-ui/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))
	return app
}

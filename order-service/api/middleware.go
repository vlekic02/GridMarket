package api

import (
	"net/http"

	log "order-service/logging"
	"order-service/model"

	"github.com/gin-gonic/gin"
)

func JsonLogger() gin.HandlerFunc {
	return func(ctx *gin.Context) {
		ctx.Next()
		status := ctx.Writer.Status()
		args := []any{
			"status_code", status,
			"method", ctx.Request.Method,
			"path", ctx.Request.RequestURI,
			"client_ip", ctx.ClientIP(),
		}
		if status >= http.StatusInternalServerError {
			log.Error("Server error encountered while executing request !", args...)
		} else if status >= http.StatusBadRequest {
			log.Warn("Client error encountered while executing request !", args...)
		} else {
			log.Debug("Executed request", args...)
		}
	}
}

func ValidateOrder() gin.HandlerFunc {
	return func(ctx *gin.Context) {
		orderRequest := new(model.OrderRequest)

		if err := ctx.ShouldBindBodyWithJSON(&orderRequest); err != nil {
			errorResponse := model.RestError{Title: "Bad Request", Status: 400, Detail: "Failed to process order request data"}
			ctx.AbortWithStatusJSON(400, errorResponse)
			return
		}
		ctx.Set("orderRequest", orderRequest)
	}
}

func ErrorHandler() gin.HandlerFunc {
	return func(ctx *gin.Context) {
		ctx.Next()
		for _, err := range ctx.Errors {
			switch e := err.Err.(type) {
			case *model.RestError:
				ctx.AbortWithStatusJSON(int(e.Status), e)
			default:
				log.Error("Unexpected error in gin context !", e)
				ctx.AbortWithStatus(500)
			}
		}
	}
}

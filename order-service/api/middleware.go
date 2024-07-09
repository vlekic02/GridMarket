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
			ctx.JSON(http.StatusBadRequest, gin.H{"error": "BadRequest"})
			ctx.Abort()
			return
		}
	}
}

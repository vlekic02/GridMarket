package api

import (
	"net/http"

	log "order-service/logging"

	"github.com/gin-gonic/gin"
)

func JsonLogger() gin.HandlerFunc {
	return func(ctx *gin.Context) {
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

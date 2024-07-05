package api

import (
	log "order-service/logging"

	"github.com/gin-gonic/gin"
)

const serverError = 500
const clientError = 400

func JsonLogger() gin.HandlerFunc {
	return func(ctx *gin.Context) {
		status := ctx.Writer.Status()
		args := []any{
			"status_code", status,
			"method", ctx.Request.Method,
			"path", ctx.Request.RequestURI,
			"client_ip", ctx.ClientIP(),
		}
		if status >= serverError {
			log.Error("Server error encountered while executing request !", args...)
		} else if status >= clientError {
			log.Warn("Client error encountered while executing request !", args...)
		} else {
			log.Debug("Executed request", args...)
		}
	}
}

package api

import (
	"encoding/json"
	"net/http"
	"strconv"

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
			errorResponse := model.NewRestError(400, "Bad Request", "Failed to process order request data")
			ctx.AbortWithStatusJSON(400, errorResponse)
			return
		}
		value, ok := ctx.Get("userInfo")
		if !ok {
			ctx.Set("orderRequest", orderRequest)
			return
		}
		userInfo := value.(*model.UserInfo)
		orderRequest.User = userInfo.Id
		ctx.Set("orderRequest", orderRequest)
	}
}

func ExtractUserInfo() gin.HandlerFunc {
	return func(ctx *gin.Context) {
		jsonData := ctx.Request.Header.Get("grid-user")
		userInfo := new(model.UserInfo)
		err := json.Unmarshal([]byte(jsonData), userInfo)
		if err != nil {
			log.Error("Failed to unmarshal user info data !", "error", err)
		} else {
			ctx.Set("userInfo", userInfo)
		}
	}
}

func ErrorHandler() gin.HandlerFunc {
	return func(ctx *gin.Context) {
		ctx.Next()
		for _, err := range ctx.Errors {
			switch e := err.Err.(type) {
			case *model.ErrorResponse:
				status, _ := strconv.Atoi(e.Errors[0].Status)
				ctx.AbortWithStatusJSON(status, e)
			default:
				log.Error("Unexpected error in gin context !", "error", e)
				ctx.AbortWithStatusJSON(500, model.NewRestError(500, "Internal Server Error", "Internal error"))
			}
		}
	}
}

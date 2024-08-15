package api

import (
	"encoding/json"
	"net/http"
	"strconv"

	"order-service/client"
	log "order-service/logging"
	"order-service/model"

	"github.com/gin-gonic/gin"
	"github.com/google/jsonapi"
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
			ctx.Error(model.NewRestError(400, "Bad Request", "Failed to process order request data"))
			ctx.Abort()
			return
		}
		value, ok := ctx.Get("userInfo")
		ctx.Set("orderRequest", orderRequest)
		if !ok {
			return
		}
		userInfo := value.(*model.UserInfo)
		orderRequest.User = userInfo.Id
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

func ValidateGetOrdersQuery(app client.ApplicationClient) gin.HandlerFunc {
	return func(ctx *gin.Context) {
		currentUser := ctx.Keys["userInfo"].(*model.UserInfo)
		userQuery := ctx.Query("user")
		if userQuery != "" {
			id, err := strconv.Atoi(userQuery)
			if err != nil {
				ctx.Error(model.NewRestError(400, "Bad Request", "User ID must be a number"))
				ctx.Abort()
				return
			}
			if id != int(currentUser.Id) && !currentUser.IsAdmin() {
				ctx.Error(model.NewRestError(403, "Unauthorized", "You don't have permission to see orders of another user"))
				ctx.Abort()
				return
			}
			ctx.Set("userId", id)
			return
		}

		applicationQuery := ctx.Query("application")
		if applicationQuery != "" {
			id, err := strconv.Atoi(applicationQuery)
			if err != nil {
				ctx.Error(model.NewRestError(400, "Bad Request", "Application ID must be a number"))
				ctx.Abort()
				return
			}
			if !currentUser.IsAdmin() {
				response, err := app.GetApplicationOwner(int32(id))
				if err != nil {
					ctx.Error(err)
					ctx.Abort()
					return
				}
				ownerId, _ := strconv.Atoi(response.ID)
				if currentUser.Id != int32(ownerId) {
					ctx.Error(model.NewRestError(403, "Unauthorized", "You don't have permission to see orders of this application"))
					ctx.Abort()
					return
				}
			}
			ctx.Set("applicationId", id)
			return
		}

		ctx.Set("userId", int(currentUser.Id))
	}
}

func JsonApiMiddleware() gin.HandlerFunc {
	return func(ctx *gin.Context) {
		ctx.Writer.Header().Set("Content-Type", jsonapi.MediaType)
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

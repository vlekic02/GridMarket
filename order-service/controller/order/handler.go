package order

import "github.com/gin-gonic/gin"

func Get(context *gin.Context) {
	context.String(200, "Hello !")
}

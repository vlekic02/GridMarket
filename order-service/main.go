package main

import (
	"log"
	"order-service/api"
)

// @title			GridMarket
// @version		1.0
// @description	Order-service api documentation
func main() {
	router := api.InitRouter()

	if err := router.Run(":8080"); err != nil {
		log.Fatal("Failed to initialize web server !", err)
	}
}

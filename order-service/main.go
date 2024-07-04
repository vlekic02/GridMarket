package main

import (
	"log"
	"order-service/api"
)

func main() {
	router := api.InitRouter()

	if err := router.Run(":8080"); err != nil {
		log.Fatal("Failed to initialize web server !", err)
	}
}

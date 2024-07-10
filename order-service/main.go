package main

import (
	"order-service/api"
	"order-service/client"
	"order-service/database"

	log "order-service/logging"
)

// @title			GridMarket
// @version		1.0
// @description	Order-service api documentation
func main() {
	postgres, err := database.InitPgDatabase()
	if err != nil {
		log.Fatal("Failed to initialize connection with database !", err)
	}
	defer postgres.Close()
	database.InitDb(postgres)
	router := api.InitRouter(client.DefaultApplicationClient)
	if err := router.Run(":8080"); err != nil {
		log.Fatal("Failed to initialize web server !", err)
	}
}

package main

import (
	"order-service/api"
	"order-service/client"
	"order-service/database"
	"order-service/messaging"

	log "order-service/logging"
)

// @title			GridMarket
// @version		1.0
// @description	Order-service api documentation
func main() {
	postgres, err := database.InitPgDatabase()
	if err != nil {
		log.Fatal("Failed to initialize connection with database !", "error", err)
	}
	defer postgres.Close()
	database.InitDb(postgres)
	err = messaging.InitPubSub()
	if err != nil {
		log.Fatal("Failed to initialize pubsub connection !", "error", err)
	}
	router := api.InitRouter(api.AppService{AppClient: &client.DefaultApplicationClient, UserClient: &client.DefaultUserClient})
	if err := router.Run(":8080"); err != nil {
		log.Fatal("Failed to initialize web server !", "error", err)
	}
}

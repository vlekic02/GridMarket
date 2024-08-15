package messaging

import (
	"context"
	"encoding/json"
	log "order-service/logging"

	"cloud.google.com/go/pubsub"
)

const (
	projectID = "gridmarket-dev"
	topicID   = "order"
)

var Msg gcpPubsub

type gcpPubsub struct {
	client     pubsub.Client
	orderTopic *pubsub.Topic
}

type successOrderEvent struct {
	User        int32 `json:"user"`
	Application int32 `json:"application"`
}

func (gcp *gcpPubsub) PublishSuccessOrder(userId int32, applicationId int32) {
	if gcp.orderTopic == nil {
		gcp.orderTopic = gcp.client.Topic(topicID)
	}
	event := successOrderEvent{User: userId, Application: applicationId}
	eventBytes, err := json.Marshal(event)
	if err != nil {
		log.Error("Failed to marshal success order event !", "error", err)
	}
	gcp.orderTopic.Publish(context.Background(), &pubsub.Message{Data: eventBytes, Attributes: map[string]string{"event": "order_success"}})
}

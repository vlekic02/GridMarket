package messaging

import (
	"context"
	"encoding/json"
	log "order-service/logging"
	"os"

	"cloud.google.com/go/pubsub"
)

const (
	projectID = "gridmarket-dev"
	topicID   = "order"
)

var Msg messageBroker

type messageBroker interface {
	PublishSuccessOrder(userId int32, applicationId int32) (string, error)
}

type gcpPubsub struct {
	client     *pubsub.Client
	orderTopic *pubsub.Topic
}

type successOrderEvent struct {
	User        int32 `json:"user"`
	Application int32 `json:"application"`
}

func InitPubSub() error {
	err := os.Setenv("PUBSUB_EMULATOR_HOST", "pub-sub:8085")
	if err != nil {
		return nil
	}
	client, err := pubsub.NewClient(context.Background(), projectID)
	if err != nil {
		return err
	}
	Msg = &gcpPubsub{client: client}
	return nil
}

func (gcp *gcpPubsub) PublishSuccessOrder(userId int32, applicationId int32) (string, error) {
	if gcp.orderTopic == nil {
		gcp.orderTopic = gcp.client.Topic(topicID)
	}
	event := successOrderEvent{User: userId, Application: applicationId}
	eventBytes, err := json.Marshal(event)
	if err != nil {
		log.Error("Failed to marshal success order event !", "error", err)
	}
	return gcp.orderTopic.Publish(context.Background(), &pubsub.Message{Data: eventBytes, Attributes: map[string]string{"event": "order_success"}}).Get(context.Background())
}

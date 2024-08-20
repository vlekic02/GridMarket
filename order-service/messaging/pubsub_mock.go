package messaging

type mockGcpPubsub struct{}

func (mock *mockGcpPubsub) PublishSuccessOrder(userId int32, applicationId int32) (string, error) {
	// Mock implementation: Do nothing
	return "1", nil
}

func InitMockPubSub() {
	Msg = &mockGcpPubsub{}
}

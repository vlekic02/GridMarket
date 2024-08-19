package messaging

type mockGcpPubsub struct{}

func (mock *mockGcpPubsub) PublishSuccessOrder(userId int32, applicationId int32) {
	// Mock implementation: Do nothing
}

func InitMockPubSub() {
	Msg = &mockGcpPubsub{}
}

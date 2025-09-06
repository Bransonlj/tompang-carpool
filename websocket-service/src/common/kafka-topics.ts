const KafkaTopics = {
  Chat: {
    CHAT_MESSAGE_SENT: "chat-message-sent",
  },
  Notification: {
    NOTIFICATION_RECEIVED: "notification-received",
  },
} as const;

export default KafkaTopics;
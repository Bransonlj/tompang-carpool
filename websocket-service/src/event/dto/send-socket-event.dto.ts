export enum SocketEventName {
  NOTIFICATION="notification",
  CHAT_MESSAGE="chat-message"
}

export type SendSocketEventDto = {
  eventName: SocketEventName.NOTIFICATION;
  targetUserId: string;
  payload: NotificationPayload;
} | {
  eventName: SocketEventName.CHAT_MESSAGE;
  targetUserId: string;
  payload: ChatMessagePayload;
}

type NotificationPayload = {
  createdAt: Date;
  notificationId: string;
  message: string;
}

type ChatMessagePayload = {
  groupChatId: string;
  senderUserId: string;
  createdAt: Date;
  messageId: string;
  message: string;
}
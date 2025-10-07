export type SendMessageDto = {
  groupId: string;
  senderId: string;
  message: string;
}

export type SenderTitle = "RIDER" | "DRIVER";

export type MessageResponseDto = {
  groupId: string;
  messageId: string;
  createdAt: string;
  senderId: string;
  senderName: string;
  senderTitle: SenderTitle;
  message: string;
}
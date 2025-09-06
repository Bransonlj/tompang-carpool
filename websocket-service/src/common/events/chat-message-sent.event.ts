export type ChatMessageSentEvent = {
  messageId: string;
  groupChatId: string;
  groupChatUsers: string[];
  senderUserId: string;
  message: string;
  createdAt: number; // avro schema timestamp is number in typescript
}
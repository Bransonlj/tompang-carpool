export type NotificationReceivedEvent = {
  notificationId: string;
  userId: string;
  message: string;
  createdAt: number; // avro schema timestamp is number in typescript
}
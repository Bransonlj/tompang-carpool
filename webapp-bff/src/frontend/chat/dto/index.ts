import { IsString } from "class-validator";

export class SendMessageDto {
  @IsString()
  groupId: string;

  @IsString()
  senderId: string;

  @IsString()
  message: string;
}

export class MessageResponseDto {
  groupId: string;
  messageId: string;
  createdAt: Date;
  senderId: string;
  senderName: string;
  senderTitle: "RIDER" | "DRIVER";
  message: string;
}
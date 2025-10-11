import { IsString } from "class-validator";

export class SendMessageDto {
  @IsString()
  groupId: string;

  @IsString()
  senderId: string;

  @IsString()
  message: string;
}
import { Type } from "class-transformer";
import { IsDate, IsString } from "class-validator";

export class ChatMessageResponseDto {
  @IsString()
  groupId: string

  @Type(() => Date)
  @IsDate()
  createdAt: Date;

  @IsString()
  messageId: string;

  @IsString()
  senderId: string;
  
  @IsString()
  message: string;
}
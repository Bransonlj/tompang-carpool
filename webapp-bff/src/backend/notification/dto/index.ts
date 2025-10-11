import { Type } from "class-transformer";
import { IsDate, IsString } from "class-validator";

export class NotificationDto {
  @IsString()
  userId: string;

  @IsDate()
  @Type(() => Date)
  createdAt: Date;

  @IsString()
  notificationId: string;

  @IsString()
  message: string;
}

export * from "./error-response.dto"
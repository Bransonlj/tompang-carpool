import { Transform, Type } from "class-transformer";
import { IsDate } from "class-validator";

export class CreateUserNotificationDto {
  userId: string;
  
  @Type(() => Date)            // 👈 transform string → Date
  @IsDate()   
  createdAt: Date;
  message: string;
}
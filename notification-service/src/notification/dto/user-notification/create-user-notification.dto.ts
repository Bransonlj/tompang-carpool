import { Type } from "class-transformer";
import { IsDate, IsString } from "class-validator";

export class CreateUserNotificationDto {
  @IsString()
  userId: string;
  
  @Type(() => Date)
  @IsDate()   
  createdAt: Date;

  @IsString()
  message: string;
}
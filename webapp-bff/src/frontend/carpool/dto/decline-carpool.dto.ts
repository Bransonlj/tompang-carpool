import { IsString } from "class-validator";

export class DeclineCarpoolDto {
  @IsString()
  carpoolId: string;
  
  @IsString()
  requestId: string;
}
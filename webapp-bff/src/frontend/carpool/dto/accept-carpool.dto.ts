import { IsString } from "class-validator";

export class AcceptCarpoolDto {
  @IsString()
  carpoolId: string;
  
  @IsString()
  requestId: string;
}
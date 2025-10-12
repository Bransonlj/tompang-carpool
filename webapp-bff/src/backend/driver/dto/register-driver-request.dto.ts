import { IsBoolean, IsString } from "class-validator";

export class RegisterDriverRequestDto {
  @IsString()
  userId: string;

  @IsString()
  vehicleRegistrationNumber: string;
  
  @IsString()
  vehicleMake: string;
  
  @IsString()
  vehicleModel: string;
  
  @IsBoolean()
  requireManualReview: boolean = false; // default
}
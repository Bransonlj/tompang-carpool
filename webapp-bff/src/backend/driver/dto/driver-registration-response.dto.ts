import { Type } from "class-transformer";
import { IsDate, IsString } from "class-validator";

export type RegistrationStatus = "PENDING" | "PENDING_MANUAL_REVIEW" | "SUCCESS" | "FAILED" | "INACTIVE";

export class DriverRegistrationResponseDto {

  @IsString()
  id: string;
  
  @IsString()
  userId: string;
  
  @IsString()
  vehicleRegistrationNumber: string;
  
  @IsString()
  vehicleMake: string;
  
  @IsString()
  vehicleModel: string;

  @Type(() => Date)
  @IsDate()
  createdAt: Date;

  @IsString()
  status: RegistrationStatus;
  rejectedReason: string | null; // null if not failed status
  signedImageUrl: string | null; // null if not provided
}
import { isString, IsString } from "class-validator";

export class RegisterDriverDto {
  @IsString()
  userId: string;

  @IsString()
  vehicleRegistrationNumber: string;

  @IsString()
  vehicleMake: string;

  @IsString()
  vehicleModel: string;
}

export class DriverRegistrationResponseDto {
    id: string;
    userId: string;
    vehicleRegistrationNumber: string;
    vehicleMake: string;
    vehicleModel: string;
    createdAt: Date;
    status: string;
    imageUrl: string;
}

export class ManualRejectRequestDto {
  @IsString()
  rejectReason: string;
}
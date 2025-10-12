export type RegisterDriverDto = {
  file: File;
  authToken: string;

  userId: string;
  vehicleRegistrationNumber: string;
  vehicleMake: string;
  vehicleModel: string;

  requireManualReview?: boolean;
}

export type DriverRegistrationStatus = "PENDING" | "PENDING_MANUAL_REVIEW" | "SUCCESS" | "FAILED" | "INACTIVE";

export type DriverRegistrationResponseDto = {
    id: string;
    userId: string;
    vehicleRegistrationNumber: string;
    vehicleMake: string;
    vehicleModel: string;
    createdAt: string;
    status: DriverRegistrationStatus;
    rejectedReason: string | undefined;
    imageUrl: string | undefined;
}
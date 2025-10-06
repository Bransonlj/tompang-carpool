export type RegisterDriverDto = {
  file: File;
  authToken: string;

  userId: string;
  vehicleRegistrationNumber: string;
  vehicleMake: string;
  vehicleModel: string;
}

export type DriverRegistrationResponseDto = {
    id: string;
    userId: string;
    vehicleRegistrationNumber: string;
    vehicleMake: string;
    vehicleModel: string;
    createdAt: string;
    status: string;
}
export class DriverRegistrationResponseDto {
    id: string;
    userId: string;
    vehicleRegistrationNumber: string;
    vehicleMake: string;
    vehicleModel: string;
    createdAt: Date;
    status: string;
    rejectedReason: string | undefined;
    imageUrl: string | undefined;
}

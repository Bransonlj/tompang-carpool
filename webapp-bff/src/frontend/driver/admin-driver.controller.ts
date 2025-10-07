import { Body, Controller, Get, Headers, Param, Post } from "@nestjs/common";
import { DriverRegistrationResponseDto, ManualRejectRequestDto } from "./dto";

@Controller("/api/driver/admin")
export class AdminDriverController {
  @Get("pending-registrations")
  async getPendingDriverRegistrations(
    @Headers("Authorization") authHeader: string,
  ): Promise<DriverRegistrationResponseDto[]> {
    console.log(authHeader);
    return [
      {
        id: "driver-reg-123",
        userId: "bob-123456",
        vehicleRegistrationNumber: "SGT1230K",
        vehicleMake: "Toyota",
        vehicleModel: "Atlas",
        createdAt: new Date(),
        status: "PENDING",
        imageUrl: "fakeimage",
      },
            {
        id: "driver-reg-456",
        userId: "tom-123456",
        vehicleRegistrationNumber: "SKK0928U",
        vehicleMake: "Hyundai",
        vehicleModel: "Buh",
        createdAt: new Date(),
        status: "PENDING",
        imageUrl: "fakeimage",
      },
      {
        id: "driver-reg-789",
        userId: "bob-123456",
        vehicleRegistrationNumber: "SJK2983J",
        vehicleMake: "Honda",
        vehicleModel: "Civic",
        createdAt: new Date(),
        status: "PENDING",
        imageUrl: "fakeimage",
      },
    ];
  }

  @Post("registration/:id/accept")
  async acceptDriverRegistration(
    @Headers("Authorization") authHeader: string,
    @Param("id") id: string,
  ): Promise<void> {
    console.log(authHeader);
  }

  @Post("registration/:id/reject")
  async rejectDriverRegistration(
    @Headers("Authorization") authHeader: string,
    @Param("id") id: string,
    @Body() dto: ManualRejectRequestDto
  ): Promise<void> {
    console.log(authHeader);
  }
}
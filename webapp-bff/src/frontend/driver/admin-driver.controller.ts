import { Body, Controller, Get, Headers, Param, Post } from "@nestjs/common";
import { DriverRegistrationResponseDto } from "./dto";
import { DriverAdminService } from "src/backend/driver/driver-admin.service";
import { ManualRejectRequestDto } from "src/backend/driver/dto";

@Controller("/api/driver/admin")
export class AdminDriverController {

  constructor(
    private driverAdminService: DriverAdminService,
  ) {}

  @Get("pending-registrations")
  async getPendingDriverRegistrations(@Headers("Authorization") authHeader: string): Promise<DriverRegistrationResponseDto[]> {
    const registrations = await this.driverAdminService.getDriverRegistrationsPendingManualReview(authHeader);
    return registrations.map(registration => ({
      ...registration,
      rejectedReason: registration.rejectedReason ?? undefined,
      imageUrl: registration.signedImageUrl ?? undefined,
    }))
  }

  @Post("registration/:id/approve")
  async approveDriverRegistration(
    @Param("id") id: string,
    @Headers("Authorization") authHeader: string,
  ): Promise<void> {
    return await this.driverAdminService.manuallyApproveDriverRegistration(id, authHeader);
  }

  @Post("registration/:id/reject")
  async rejectDriverRegistration(
    @Param("id") id: string,
    @Body() dto: ManualRejectRequestDto,
    @Headers("Authorization") authHeader: string,
  ): Promise<void> {
    return await this.driverAdminService.manuallyRejectDriverRegistration(dto, id, authHeader);
  }
}
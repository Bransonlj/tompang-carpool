import { Body, Controller, Get, Headers, Param, Post, UploadedFile, UseInterceptors } from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { DriverRegistrationResponseDto } from './dto';
import { ParseAndValidateJsonPipe } from 'src/pipes/parse-validate-json.pipe';
import { DriverService } from 'src/backend/driver/driver.service';
import { RegisterDriverRequestDto } from 'src/backend/driver/dto';

@Controller('api/driver')
export class DriverController {

  constructor(
    private driverService: DriverService,
  ) {}
  
  @Get('registration/user/:userid')
  async getRegistrationsByUserId(
    @Param("userid") userId: string, 
    @Headers("Authorization") authHeader: string,
  ): Promise<DriverRegistrationResponseDto[]> {
    const registrations = await this.driverService.getDriverRegistrationsByUserId(userId, authHeader);
    return registrations.map(registration => ({
      ...registration,
      rejectedReason: registration.rejectedReason ?? undefined,
      imageUrl: registration.signedImageUrl ?? undefined,
    }));
  }

  @Get('registration/:rid')
  async getRegistrationById(
    @Param("rid") rid: string, 
    @Headers("Authorization") authHeader: string,
  ): Promise<DriverRegistrationResponseDto> {
    const registration = await this.driverService.getDriverRegistrationById(rid, authHeader);
    return {
      ...registration,
      rejectedReason: registration.rejectedReason ?? undefined,
      imageUrl: registration.signedImageUrl ?? undefined,
    };
  }

  @Post('register')
  @UseInterceptors(FileInterceptor('file'))
  async uploadProfilePicture(
    @UploadedFile() file: Express.Multer.File,
    @Body('dto', new ParseAndValidateJsonPipe(RegisterDriverRequestDto)) dto: RegisterDriverRequestDto,
    @Headers("Authorization") authHeader: string,
  ) {
    console.log(dto)
    return await this.driverService.createDriverRegistration(dto, file, authHeader);
  }
}

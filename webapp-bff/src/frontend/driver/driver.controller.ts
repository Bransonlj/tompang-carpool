import { Body, Controller, Get, Param, Post, UploadedFile, UseInterceptors } from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { DriverRegistrationResponseDto, RegisterDriverDto } from './dto';
import { ParseAndValidateJsonPipe } from 'src/pipes/parse-validate-json.pipe';

@Controller('api/driver')
export class DriverController {
  
  @Get('registration/:userid')
  async getRegistrationByUserId(@Param("userid") userId: string): Promise<DriverRegistrationResponseDto[]> {
    return [
      {
        id: "driver-reg-123",
        userId: "bob-123456",
        vehicleRegistrationNumber: "SGT1230K",
        vehicleMake: "Toyota",
        vehicleModel: "Atlas",
        createdAt: new Date(),
        status: "PENDING" // PENDING, PENDING_MANUAL_REVIEW, SUCCESS, FAILED
      },
    ];
  }

  @Post('register')
  @UseInterceptors(FileInterceptor('file'))
  async uploadProfilePicture(
    @UploadedFile() file: Express.Multer.File,
    @Body('dto', new ParseAndValidateJsonPipe(RegisterDriverDto))
    dto: RegisterDriverDto,
  ) {
    console.log(`uploaded: ${file.filename} ${file.size} ${JSON.stringify(dto)}`);
  }
}

import { HttpModule } from '@nestjs/axios';
import { Module } from '@nestjs/common';
import { CarpoolService } from './carpool/carpool.service';
import { RideRequestService } from './carpool/ride-request.service';
import { UserService } from './user/user.service';
import { HttpLoggingService } from './http-logging.service';

@Module({
  imports: [HttpModule.register({
    baseURL: "http://localhost:4500",
  })],
  providers: [
    HttpLoggingService,
    CarpoolService, 
    RideRequestService, 
    UserService
  ],
  exports: [CarpoolService,  RideRequestService, UserService],
})
export class BackendModule {}

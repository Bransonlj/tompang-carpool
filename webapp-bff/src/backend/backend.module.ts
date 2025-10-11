import { HttpModule } from '@nestjs/axios';
import { Module } from '@nestjs/common';
import { CarpoolService } from './carpool/carpool.service';
import { RideRequestService } from './carpool/ride-request.service';
import { UserService } from './user/user.service';
import { HttpLoggingService } from './http-logging.service';
import { NotificationService } from './notification/notification.service';

@Module({
  imports: [HttpModule.register({
    baseURL: "http://localhost:4500", // api-gateway
  })],
  providers: [
    HttpLoggingService, // logger
    CarpoolService, 
    RideRequestService, 
    UserService, 
    NotificationService,
  ],
  exports: [
    CarpoolService,  
    RideRequestService,
    UserService,
    NotificationService,
  ],
})
export class BackendModule {}

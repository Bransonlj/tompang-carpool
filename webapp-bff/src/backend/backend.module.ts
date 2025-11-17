import { HttpModule } from '@nestjs/axios';
import { Module } from '@nestjs/common';
import { CarpoolService } from './carpool/carpool.service';
import { RideRequestService } from './carpool/ride-request.service';
import { UserService } from './user/user.service';
import { HttpLoggingService } from './http-logging.service';
import { NotificationService } from './notification/notification.service';
import { ChatService } from './chat/chat.service';
import { DriverService } from './driver/driver.service';
import { DriverAdminService } from './driver/driver-admin.service';
import appConfig, { AppConfig } from 'src/config/app.config';

@Module({
  imports: [
    HttpModule.registerAsync({
      inject: [appConfig.KEY],   // inject your config provider
      useFactory: (config: AppConfig) => ({
        baseURL: config.apiUrl, // api-gateway
      }),
    }),
  ],
  providers: [
    HttpLoggingService, // logger
    CarpoolService, 
    RideRequestService, 
    UserService, 
    NotificationService, 
    ChatService, 
    DriverService,
    DriverAdminService,
  ],
  exports: [
    CarpoolService,  
    RideRequestService,
    UserService,
    NotificationService,
    ChatService,
    DriverService,
    DriverAdminService,
  ],
})
export class BackendModule {}

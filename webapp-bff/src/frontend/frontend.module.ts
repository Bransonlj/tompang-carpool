import { Module } from '@nestjs/common';
import { CarpoolController } from './carpool/carpool.controller';
import { BackendModule } from 'src/backend/backend.module';
import { RideRequestController } from './ride-request/ride-request.controller';
import { AuthController } from './auth/auth.controller';
import { UserController } from './user/user.controller';
import { DriverController } from './driver/driver.controller';
import { AdminDriverController } from './driver/admin-driver.controller';

@Module({
  imports: [BackendModule],
  controllers: [
    CarpoolController, 
    RideRequestController, 
    AuthController, 
    UserController, 
    DriverController,
    AdminDriverController,
  ],
  providers: []
})
export class FrontendModule {}

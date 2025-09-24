import { Module } from '@nestjs/common';
import { CarpoolController } from './carpool/carpool.controller';
import { BackendModule } from 'src/backend/backend.module';

@Module({
  imports: [BackendModule],
  controllers: [CarpoolController]
})
export class FrontendModule {}

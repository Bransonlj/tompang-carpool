import { HttpModule } from '@nestjs/axios';
import { Module } from '@nestjs/common';
import { CarpoolService } from './carpool/carpool.service';

@Module({
  imports: [HttpModule],
  providers: [CarpoolService],
  exports: [CarpoolService],
})
export class BackendModule {}

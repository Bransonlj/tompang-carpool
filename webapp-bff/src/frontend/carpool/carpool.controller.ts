import { Controller, Get, Param } from '@nestjs/common';
import { CarpoolService } from 'src/backend/carpool/carpool.service';

@Controller('api/carpool')
export class CarpoolController {
  constructor(
    private carpoolService: CarpoolService
  ) {}

  @Get(":id")
  async getCarpool(@Param('id') id: string) {
    return this.carpoolService.getCarpoolById(id);
  }
}

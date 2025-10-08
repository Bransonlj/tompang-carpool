import { HttpService } from '@nestjs/axios';
import { Injectable, Logger } from '@nestjs/common';
import { CarpoolDetailedDto } from './dto';
import { firstValueFrom } from 'rxjs';
import { plainToInstance } from 'class-transformer';
import { handleApiError } from '../util';

@Injectable()
export class CarpoolService {
  private readonly logger = new Logger(CarpoolService.name);
  constructor(private readonly http: HttpService) {}

  async getCarpoolById(id: string): Promise<CarpoolDetailedDto> {
    try {
      const response = await firstValueFrom(
        this.http.get(`http://localhost:4000/api/carpool/query/${id}`),
      );

      return plainToInstance(CarpoolDetailedDto, response.data, {
        enableImplicitConversion: true,
      });
    } catch (error) {
      throw handleApiError(error, this.logger.error);
    }
  }
}

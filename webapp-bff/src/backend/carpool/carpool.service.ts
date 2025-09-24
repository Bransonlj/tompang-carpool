import { HttpService } from '@nestjs/axios';
import { HttpException, Injectable, Logger } from '@nestjs/common';
import { CarpoolDetailedDto } from './dto';
import { firstValueFrom } from 'rxjs';
import { plainToInstance } from 'class-transformer';
import { AxiosError } from 'axios';
import { ErrorResponseDto } from './dto/error-response.dto';

@Injectable()
export class CarpoolService {
  private readonly logger = new Logger(CarpoolService.name);
  constructor(private readonly http: HttpService) {}

  private handleError(error: any): HttpException {
      // Generate the error response message
      let message: string;
      let status: number = 500; // default to 500 if unknown error
      if (error instanceof AxiosError) {
        if (error.response) {
          // Received response from server with status code outside 2xx (eg. 4xx, 500)
          message = (error.response.data as ErrorResponseDto).message;
          status = error.response.status;
        } else {
          message = `No response from server: ${error.message}`;
        }
      } else {
        // not unknown error type
        message = error.message;
      }
  
      this.logger.error(`Error querying ${status}: ${message}`);
      return new HttpException(message, status);
  }

  async getCarpoolById(id: string): Promise<CarpoolDetailedDto> {
    try {
      const response = await firstValueFrom(
        this.http.get(`http://localhost:4000/api/carpool/query/${id}`),
      );

      return plainToInstance(CarpoolDetailedDto, response.data, {
        enableImplicitConversion: true,
      });
    } catch (error) {
      throw this.handleError(error);
    }
  }
}

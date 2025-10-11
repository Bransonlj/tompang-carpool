import { HttpException, Injectable, Logger } from '@nestjs/common';
import { ErrorResponseDto, NotificationDto } from './dto';
import { firstValueFrom } from 'rxjs';
import { HttpService } from '@nestjs/axios';
import { AxiosError } from 'axios';
import { plainToInstance } from 'class-transformer';
import { authorizationHeader } from '../util';

@Injectable()
export class NotificationService {
  private readonly logger = new Logger(NotificationService.name);
  constructor(private readonly http: HttpService) {}
  
  async getNotificationsByUserId(id: string, authorization: string): Promise<NotificationDto[]> {
    try {
      const response = await firstValueFrom(
        this.http.get<any[]>(`api/notification/user/${id}`, authorizationHeader(authorization)),
      );

      return plainToInstance(NotificationDto, response.data, {
        enableImplicitConversion: true,
      });
    } catch (error) {
      throw this.handleError(error);
    }
  }

  private handleError(error: any): HttpException {
    // Generate the error response message
    let message: string;
    let status: number = 500; // default to 500 if unknown error
    if (error instanceof AxiosError) {
      if (error.response) {
        // Received response from server with status code outside 2xx (eg. 4xx, 500)
        message = (error.response.data as ErrorResponseDto).message ?? "unknown error message";
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
}

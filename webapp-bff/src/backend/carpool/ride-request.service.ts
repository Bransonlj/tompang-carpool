import { HttpService } from '@nestjs/axios';
import { HttpException, Injectable, Logger } from '@nestjs/common';
import { CreateRideRequestCommand, RideRequestDetailedDto, RideRequestSummaryDto } from './dto';
import { firstValueFrom } from 'rxjs';
import { authorizationHeader } from '../util';
import { plainToInstance } from 'class-transformer';
import { AxiosError } from 'axios';
import { ErrorResponseDto } from './dto/error-response.dto';

@Injectable()
export class RideRequestService {
  private readonly logger = new Logger(RideRequestService.name);
  constructor(private readonly http: HttpService) {}

  async getRideRequestById(id: string, authorization: string): Promise<RideRequestDetailedDto> {
    try {
      const response = await firstValueFrom(
        this.http.get(`api/ride-request/query/${id}`, authorizationHeader(authorization)),
      );

      return plainToInstance(RideRequestDetailedDto, response.data, {
        enableImplicitConversion: true,
      });
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getRideRequestsByRiderId(id: string, authorization: string): Promise<RideRequestSummaryDto[]> {
    try {
      const response = await firstValueFrom(
        this.http.get<any[]>(`api/ride-request/query/rider/${id}`, authorizationHeader(authorization)),
      );

      return plainToInstance(RideRequestSummaryDto, response.data, {
        enableImplicitConversion: true,
      });
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async createRideRequest(command: CreateRideRequestCommand, authorization: string): Promise<void> {
    try {
      const response = await firstValueFrom(
        this.http.post(`api/ride-request/command/create`, command, authorizationHeader(authorization)),
      );
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

import { HttpService } from '@nestjs/axios';
import { HttpException, Injectable, Logger } from '@nestjs/common';
import { AcceptCarpoolRequestCommand, CarpoolDetailedDto, CarpoolSummaryDto, CreateCarpoolCommand, DeclineCarpoolRequestCommand } from './dto';
import { firstValueFrom } from 'rxjs';
import { plainToInstance } from 'class-transformer';
import { authorizationHeader } from '../util';
import { ErrorResponseDto } from './dto/error-response.dto';
import { AxiosError } from 'axios';

@Injectable()
export class CarpoolService {
  private readonly logger = new Logger(CarpoolService.name);
  constructor(private readonly http: HttpService) {}

  async getCarpoolById(id: string, authorization: string): Promise<CarpoolDetailedDto> {
    try {
      const response = await firstValueFrom(
        this.http.get(`api/carpool/query/${id}`, authorizationHeader(authorization)),
      );

      return plainToInstance(CarpoolDetailedDto, response.data, {
        enableImplicitConversion: true,
      });
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getCarpoolsByUserId(uid: string, authorization: string): Promise<CarpoolSummaryDto[]> {
    try {
      const response = await firstValueFrom(
        this.http.get<any[]>(`api/carpool/query/driver/${uid}`, authorizationHeader(authorization)),
      );

      return plainToInstance(CarpoolSummaryDto, response.data, {
        enableImplicitConversion: true,
      });
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async createCarpool(command: CreateCarpoolCommand, authorization: string): Promise<void> {
    try {
      const response = await firstValueFrom(
        this.http.post(`api/carpool/command/create`, command, authorizationHeader(authorization))
      );
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async acceptRequest(command: AcceptCarpoolRequestCommand, authorization: string): Promise<void> {
    try {
      const response = await firstValueFrom(
        this.http.post(`api/carpool/command/request/accept`, command, authorizationHeader(authorization))
      );
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async declineRequest(command: DeclineCarpoolRequestCommand, authorization: string): Promise<void> {
    try {
      const response = await firstValueFrom(
        this.http.post(`api/carpool/command/request/decline`, command, authorizationHeader(authorization))
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

    this.logger.error(`Error querying ${status}: ${message}`, error.response.data);
    return new HttpException(message, status);
  }
}

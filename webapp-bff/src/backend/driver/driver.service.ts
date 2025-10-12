import { HttpService } from '@nestjs/axios';
import { HttpException, Injectable, Logger } from '@nestjs/common';
import { AxiosError } from 'axios';
import { DriverRegistrationResponseDto, ErrorResponseDto, RegisterDriverRequestDto } from './dto';
import { firstValueFrom } from 'rxjs';
import { authorizationHeader } from '../util';
import { plainToInstance } from 'class-transformer';
import * as FormData from 'form-data';

@Injectable()
export class DriverService {
  private readonly logger = new Logger(DriverService.name);
  constructor(private readonly http: HttpService) {}

  async getDriverRegistrationsByUserId(userId: string, authorization: string): Promise<DriverRegistrationResponseDto[]> {
    try {
      const response = await firstValueFrom(
        this.http.get<any[]>(`api/driver/user/${userId}`, authorizationHeader(authorization)),
      );

      return plainToInstance(DriverRegistrationResponseDto, response.data, {
        enableImplicitConversion: true,
      });
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getDriverRegistrationById(id: string, authorization: string): Promise<DriverRegistrationResponseDto> {
    try {
      const response = await firstValueFrom(
        this.http.get(`api/driver/${id}`, authorizationHeader(authorization)),
      );

      return plainToInstance(DriverRegistrationResponseDto, response.data, {
        enableImplicitConversion: true,
      });
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async createDriverRegistration(dto: RegisterDriverRequestDto, file: Express.Multer.File, authorization: string) {
    try {
      const formData = new FormData();
      formData.append("image", Buffer.from(file.buffer), file.originalname);
      formData.append("data", JSON.stringify(dto), {
        contentType: "application/json",
      });
      const response = await firstValueFrom(
        this.http.post(`api/driver/register`, formData, authorizationHeader(authorization, "multipart/form-data")),
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

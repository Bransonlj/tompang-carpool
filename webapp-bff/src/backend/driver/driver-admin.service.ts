import { HttpService } from "@nestjs/axios";
import { HttpException, Injectable, Logger } from "@nestjs/common";
import { AxiosError } from "axios";
import { DriverRegistrationResponseDto, ErrorResponseDto, ManualRejectRequestDto } from "./dto";
import { firstValueFrom } from "rxjs";
import { authorizationHeader } from "../util";
import { plainToInstance } from "class-transformer";

@Injectable()
export class DriverAdminService {
  private readonly logger = new Logger(DriverAdminService.name);
  constructor(private readonly http: HttpService) {}

  async getDriverRegistrationsPendingManualReview(authorization: string): Promise<DriverRegistrationResponseDto[]> {
    try {
      const response = await firstValueFrom(
        this.http.get<any[]>(`api/driver/admin/pending-review`, authorizationHeader(authorization)),
      );

      return plainToInstance(DriverRegistrationResponseDto, response.data, {
        enableImplicitConversion: true,
      });
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async manuallyApproveDriverRegistration(registrationId: string, authorization: string): Promise<void> {
    try {
      const response = await firstValueFrom(
        this.http.post(`api/driver/admin/${registrationId}/approve`, {}, authorizationHeader(authorization)),
      );
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async manuallyRejectDriverRegistration(dto: ManualRejectRequestDto, registrationId: string, authorization: string): Promise<void> {
    try {
      const response = await firstValueFrom(
        this.http.post(`api/driver/admin/${registrationId}/reject`, dto, authorizationHeader(authorization)),
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
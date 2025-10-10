import { HttpService } from '@nestjs/axios';
import { HttpException, Injectable, Logger } from '@nestjs/common';
import { AxiosError } from 'axios';
import { ErrorResponseDto, LoginRequestDto, LoginResponseDto, RegisterRequestDto, UserProfileBatchResponseDto, UserProfileResponseDto } from './dto';
import { firstValueFrom } from 'rxjs';
import { authorizationHeader } from '../util';
import { plainToInstance } from 'class-transformer';
import * as FormData from 'form-data';

@Injectable()
export class UserService {
  private readonly logger = new Logger(UserService.name);
  constructor(private readonly http: HttpService) {}

  async getUserProfileById(id: string, authorization: string): Promise<UserProfileResponseDto> {
    try {
      const response = await firstValueFrom(
        this.http.get(`api/user/profile/${id}`, authorizationHeader(authorization)),
      );

      return plainToInstance(UserProfileResponseDto, response.data, {
        enableImplicitConversion: true,
      });
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async getUserProfilesFromIdsByBatch(ids: Set<string>, authorization: string): Promise<UserProfileBatchResponseDto> {
    try {
      const response = await firstValueFrom(
        this.http.post(`api/user/profile/batch`, Array.from(ids), authorizationHeader(authorization)),
      );

      return plainToInstance(UserProfileBatchResponseDto, response.data, {
        enableImplicitConversion: true,
      });
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async uploadUserProfilePicture(file: Express.Multer.File, userId: string, authorization: string): Promise<void> {
    try {
      const formData = new FormData();
      formData.append("file", Buffer.from(file.buffer), file.originalname);
      formData.append("userId", userId);
      const response = await firstValueFrom(
        this.http.post(`api/user/profile/picture`, formData, authorizationHeader(authorization, "multipart/form-data")),
      );

    } catch (error) {
      throw this.handleError(error);
    }
  }

  async register(dto: RegisterRequestDto): Promise<void> {
    try {
      const response = await firstValueFrom(
        this.http.post(`api/user/auth/public/register`, dto),
      );
    } catch (error) {
      throw this.handleError(error);
    }
  }

  async login(dto: LoginRequestDto): Promise<LoginResponseDto> {
    try {
      const response = await firstValueFrom(
        this.http.post(`api/user/auth/public/login`, dto),
    );

      return plainToInstance(LoginResponseDto, response.data, {
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

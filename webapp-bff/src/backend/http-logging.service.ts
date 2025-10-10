import { HttpService } from "@nestjs/axios";
import { Injectable, Logger, OnModuleInit } from "@nestjs/common";
import { AxiosRequestConfig, RawAxiosRequestConfig } from "axios";

@Injectable()
export class HttpLoggingService implements OnModuleInit {
  private readonly logger = new Logger('BackendHttp');

  constructor(private readonly httpService: HttpService) {}

  onModuleInit() {
    const axios = this.httpService.axiosRef;

    // Request interceptor
    axios.interceptors.request.use((config) => {
      this.logger.log(`➡️  [${config.method?.toUpperCase()}] ${config.url}`);
      return config;
    });
  }
}
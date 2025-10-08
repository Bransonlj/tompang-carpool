import { HttpException } from "@nestjs/common";
import { AxiosError } from "axios";
import { ErrorResponseDto } from "../carpool/dto/error-response.dto";

export function handleApiError(error: any, log?: (message: string) => void): HttpException {
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
  
      log?.(`Error querying ${status}: ${message}`);
      return new HttpException(message, status);
  }
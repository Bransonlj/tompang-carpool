import axios, { AxiosError, type AxiosRequestConfig } from "axios";
import type { ErrorResponseDto } from "./error-response.dto";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
});

api.interceptors.response.use(
  (response) => response.data,
  (error) => {      
    // Generate the error response message
    let message: string;
    let status: number;
    if (error instanceof AxiosError) {
      if (error.response) {
        // Received response from server with status code outside 2xx (eg. 4xx, 500)
        message = (error.response.data as ErrorResponseDto).message;
        status = error.response.status;
      } else {
        message = `No response from server: ${error.message}`;
        status = 500;
      }
    } else {
      // not unknown error type
      message = error.message;
      status = -1;
    }

    console.error(`Error querying ${status}: ${message}`);
    throw new Error(message);
  }
);

export function authHeader(token: string, contentType='application/json'): AxiosRequestConfig {
  return {
    headers: {
      "Authorization": `Bearer ${token}`,
      "Content-Type": contentType,
    }
  };
}

export default api;
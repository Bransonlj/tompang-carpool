import { AxiosRequestConfig } from "axios";

export function authorizationHeader(authorization: string, contentType='application/json'): AxiosRequestConfig {
  return {
    headers: {
      "Authorization": authorization,
      "Content-Type": contentType,
    }
  };
}
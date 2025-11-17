import { registerAs } from "@nestjs/config";
import { IsInt, IsOptional, IsString, Max, Min } from "class-validator";
import { validateConfig } from "./utils";

class EnvironmentVariablesValidator {
  @IsInt()
  @Min(0)
  @Max(65536)
  @IsOptional()
  APP_PORT?: number;

  @IsString()
  API_URL: string;

}

export type AppConfig = {
  port: number;
  apiUrl: string;
}


/**
 * Util function to get env variables without ConfigService dependency injection.
 * @returns 
 */
export function getConfig(): AppConfig {
  return {
    port: parseInt(process.env.APP_PORT ?? "4700"),
    apiUrl: process.env.API_URL ?? "http://localhost:4500",
  };
}

export default registerAs<AppConfig>('app', () => {
  console.info(`Registering app from environment variables`);
  validateConfig(process.env, EnvironmentVariablesValidator);
  return getConfig();
});
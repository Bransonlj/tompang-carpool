import { registerAs } from "@nestjs/config";
import { IsBooleanString, IsInt, IsOptional, Max, Min } from "class-validator";
import { validateConfig } from "./utils";

class EnvironmentVariablesValidator {
  @IsInt()
  @Min(0)
  @Max(65536)
  @IsOptional()
  APP_PORT?: number;

  @IsOptional()
  KAFKA_BROKER?: string;

  @IsOptional()
  SCHEMA_REGISTRY_URL?: string;

}

export type ConnectionConfig = {
  port: number;
  kafkaBroker: string;
  schemaRegistryUrl: string;
}


/**
 * Util function to get env variables without ConfigService dependency injection.
 * @returns 
 */
export function getConfig(): ConnectionConfig {
  return {
    port: parseInt(process.env.APP_PORT ?? "4100"), // default to 4100
    kafkaBroker: process.env.KAFKA_BROKER ?? "localhost:9092",
    schemaRegistryUrl: process.env.SCHEMA_REGISTRY_URL ?? "http://localhost:8081",
  };
}

export default registerAs<ConnectionConfig>('connection', () => {
  console.info(`Registering Connections from environment variables`);
  validateConfig(process.env, EnvironmentVariablesValidator);
  return getConfig();
});
import { registerAs } from "@nestjs/config";
import { IsInt, IsOptional, Max, Min } from "class-validator";
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

  @IsOptional()
  CASSANDRA_CONTACT_POINTS?: string;

  @IsInt()
  @Min(0)
  @Max(65536)
  @IsOptional()
  CASSANDRA_PORT?: number;
}

export type ConnectionConfig = {
  port: number;
  kafkaBroker: string;
  schemaRegistryUrl: string;
  cassandraContactPoints: string[];
  cassandraPort: number;
}


/**
 * Util function to get env variables without ConfigService dependency injection.
 * @returns 
 */
export function getConfig(): ConnectionConfig {
  return {
    port: parseInt(process.env.APP_PORT ?? "4007"),
    kafkaBroker: process.env.KAFKA_BROKER ?? "localhost:9092",
    schemaRegistryUrl: process.env.SCHEMA_REGISTRY_URL ?? "http://localhost:8081",
    cassandraContactPoints: [process.env.CASSANDRA_CONTACT_POINTS ?? "localhost"],
    cassandraPort: parseInt(process.env.CASSANDRA_PORT ?? "9042"),
  };
}

export default registerAs<ConnectionConfig>('connection', () => {
  console.info(`Registering Connections from environment variables`);
  validateConfig(process.env, EnvironmentVariablesValidator);
  return getConfig();
});
import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { MicroserviceOptions, Transport } from '@nestjs/microservices';
import { getConfig as getConnectionConfig } from './config/connection.config';

async function bootstrap() {
  const appConfig = getConnectionConfig();

  const app = await NestFactory.create(AppModule);
  const microservice = app.connectMicroservice<MicroserviceOptions>({
    transport: Transport.KAFKA,
    options: {
      client: {
        brokers: [appConfig.kafkaBroker],
      }
    }
  });
  await app.startAllMicroservices();
  await app.listen(appConfig.port);
  
}
bootstrap();

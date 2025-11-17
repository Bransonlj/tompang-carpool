import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { ValidationPipe } from '@nestjs/common';
import { getConfig as getConnectionConfig } from './config/connection.config';

async function bootstrap() {
  const connectionConfig = getConnectionConfig();

  const app = await NestFactory.create(AppModule);
  app.useGlobalPipes(new ValidationPipe({
    transform: true,
  }));
  await app.listen(connectionConfig.port);
}
bootstrap();

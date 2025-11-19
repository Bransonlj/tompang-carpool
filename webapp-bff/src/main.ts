import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { getConfig as getAppConfig } from './config/app.config';
import { ValidationPipe } from '@nestjs/common';

async function bootstrap() {
  const appConfig = getAppConfig()
  const app = await NestFactory.create(AppModule, {
    cors: true,
  });
  app.useGlobalPipes(new ValidationPipe({
    transform: true,
  }));
  await app.listen(appConfig.port);
}
bootstrap();

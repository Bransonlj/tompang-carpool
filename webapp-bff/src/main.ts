import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { getConfig as getAppConfig } from './config/app.config';

async function bootstrap() {
  const appConfig = getAppConfig()
  const app = await NestFactory.create(AppModule, {
    cors: true,
  });
  await app.listen(appConfig.port);
}
bootstrap();

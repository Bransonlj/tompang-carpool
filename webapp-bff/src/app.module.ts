import { MiddlewareConsumer, Module, NestModule } from '@nestjs/common';
import { BackendModule } from './backend/backend.module';
import { FrontendModule } from './frontend/frontend.module';
import { LoggingMiddleware } from './logging.middleware';

@Module({
  imports: [
    BackendModule,
    FrontendModule,
  ],
})
export class AppModule implements NestModule {
  configure(consumer: MiddlewareConsumer) {
    consumer.apply(LoggingMiddleware).forRoutes('*'); // apply to all routes
  }
}

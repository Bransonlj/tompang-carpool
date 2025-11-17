import { Module } from '@nestjs/common';
import { EventModule } from './event/event.module';
import { ConfigModule } from '@nestjs/config';
import connectionConfig from './config/connection.config';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      load: [
        connectionConfig,
      ],
    }),
    EventModule,
  ],
})
export class AppModule {}

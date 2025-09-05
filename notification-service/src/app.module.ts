import { Module } from '@nestjs/common';
import { NotificationModule } from './notification/notification.module';
import { MessageModule } from './message/message.module';

@Module({
  imports: [NotificationModule, MessageModule],
})
export class AppModule {}

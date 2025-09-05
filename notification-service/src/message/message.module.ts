import { Module } from '@nestjs/common';
import { MessageService } from './message.service';
import { NotificationModule } from 'src/notification/notification.module';
import { KafkaMessageConsumer } from './kakfa-message.consumer';

@Module({
  imports: [NotificationModule],
  controllers: [],
  providers: [MessageService, KafkaMessageConsumer]
})
export class MessageModule {}

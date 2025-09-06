import { Module } from '@nestjs/common';
import { MessageService } from './message.service';
import { NotificationModule } from 'src/notification/notification.module';
import { KafkaMessageConsumer } from './kakfa-message.consumer';
import { SchemaRegistryModule } from 'src/kafka/schema-registry/schema-registry.module';

@Module({
  imports: [NotificationModule, SchemaRegistryModule],
  controllers: [],
  providers: [MessageService, KafkaMessageConsumer]
})
export class MessageModule {}

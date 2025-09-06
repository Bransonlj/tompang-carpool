import { Module } from '@nestjs/common';
import { NotificationModule } from './notification/notification.module';
import { MessageModule } from './message/message.module';
import { SchemaRegistryModule } from './kafka/schema-registry/schema-registry.module';
import { KafkaProducerModule } from './kafka/kafka-producer/kafka-producer.module';

@Module({
  imports: [
    SchemaRegistryModule,
    KafkaProducerModule,
    NotificationModule, 
    MessageModule, 
  ],
})
export class AppModule {}

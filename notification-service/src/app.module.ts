import { Module } from '@nestjs/common';
import { NotificationModule } from './notification/notification.module';
import { MessageModule } from './message/message.module';
import { SchemaRegistryModule } from './kafka/schema-registry/schema-registry.module';
import { KafkaProducerModule } from './kafka/kafka-producer/kafka-producer.module';
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
    SchemaRegistryModule,
    KafkaProducerModule,
    NotificationModule, 
    MessageModule, 
  ],
})
export class AppModule {}

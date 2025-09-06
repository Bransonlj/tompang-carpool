import { Module } from '@nestjs/common';
import { NotificationRepository } from './notification.repository';
import { NotificationController } from './notification.controller';
import { NotificationService } from './notification.service';
import { SchemaRegistryModule } from 'src/kafka/schema-registry/schema-registry.module';
import { KafkaProducerModule } from 'src/kafka/kafka-producer/kafka-producer.module';

@Module({
  imports: [SchemaRegistryModule, KafkaProducerModule],
  providers: [NotificationRepository, NotificationService],
  controllers: [NotificationController],
  exports: [NotificationService],
})
export class NotificationModule {}

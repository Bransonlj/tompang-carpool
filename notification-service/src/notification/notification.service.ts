import { Injectable, OnModuleInit } from '@nestjs/common';
import { NotificationRepository } from './notification.repository';
import { CreateUserNotificationDto } from './dto/user-notification';
import { KafkaTopic } from 'src/kafka/topics';
import { NotificationReceivedEvent } from 'src/kafka/event/notification';
import { KafkaProducerService } from 'src/kafka/kafka-producer/kafka-producer.service';
import { SchemaRegistryService } from 'src/kafka/schema-registry/schema-registry.service';

@Injectable()
export class NotificationService implements OnModuleInit {
  
  notificationReceivedSchemaId: number;

  constructor(
    private kafkaProducerService: KafkaProducerService,
    private registryService: SchemaRegistryService,
    private repository: NotificationRepository,
  ) {}
  
  async onModuleInit() {
    this.notificationReceivedSchemaId = await this.registryService.loadTopicSchema(KafkaTopic.NOTIFICATION_RECEIVED);
  }

  /**
   * Creates the notification record with the repository and publishes the notification-received kafka event.
   * @param createDto 
   * @returns 
   */
  async createNotification(createDto: CreateUserNotificationDto) {
    const createdNotification = await this.repository.saveNotification(createDto);
    const event: NotificationReceivedEvent = {
      ...createdNotification,
      createdAt: createdNotification.createdAt.getTime(),
    };
    const encodedValue = await this.registryService.registry.encode(this.notificationReceivedSchemaId, event);
    await this.kafkaProducerService.producer.send({
      topic: KafkaTopic.NOTIFICATION_RECEIVED,
      messages: [{ value: encodedValue }],
    });
    return createdNotification;
  }

  async getNotificationsByUser(userId: string) {
    return await this.repository.getNotificationsByUserSorted(userId);
  }

}

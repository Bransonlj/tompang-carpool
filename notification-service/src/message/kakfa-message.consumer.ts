import { Controller, Logger, OnModuleDestroy, OnModuleInit } from '@nestjs/common';
import { MessageService } from './message.service';
import { DomainEvent } from './domain-event';
import { Consumer } from 'kafkajs';
import { KAFKA_TOPIC_EVENT_MAP } from './message.config';
import { SchemaRegistryService } from 'src/kafka/schema-registry/schema-registry.service';
import kafka from 'src/kafka/kafka';

@Controller()
export class KafkaMessageConsumer implements OnModuleInit, OnModuleDestroy {
  private readonly logger = new Logger(KafkaMessageConsumer.name);
  private readonly consumer: Consumer
  constructor(
    private messageService: MessageService,
    private registryService: SchemaRegistryService,
  ) {
    this.consumer = kafka.consumer({ groupId: 'notification-service' });
  }

  /**
   * Decodes the kafka event message payload with schema registry and creates the notification from the payload data.
   */
  private async handleEvent<T extends DomainEvent>(
    ctor: new (payload: any) => T,
    message: Buffer,
    timestamp: String,
  ): Promise<T> {
    const decoded = await this.registryService.registry.decode(message);
    const event = new ctor(decoded);
    const eventDate = new Date(Number(timestamp));
    this.messageService.createNotificationFromEvent(event, eventDate);
    return event;
  }

  /**
   * Subscibe to topics and handle the events specified in "kafka-message.consumer.ts"
   */
  async onModuleInit() {
    await this.consumer.connect();
    await this.consumer.subscribe({ topics: Array.from(KAFKA_TOPIC_EVENT_MAP.keys()), fromBeginning: true });
    await this.consumer.run({
      eachMessage: async ({ topic, partition, message }) => {
        const eventCtor = KAFKA_TOPIC_EVENT_MAP.get(topic);
        if (eventCtor !== undefined && message.value !== null) {
          this.logger.log(
            `Received message on topic "${topic}" (partition ${partition}, offset ${message.offset}, timestamp ${message.timestamp})`,
            message.value
          );
          this.handleEvent(eventCtor, message.value, message.timestamp);
        } else {
          this.logger.warn(
            `No handler found for message on topic "${topic}" (partition ${partition}, offset ${message.offset})`
          );
        }
      },
    });
  }

  async onModuleDestroy() {
    await this.consumer.disconnect();
  }

}

import { SchemaRegistry } from '@kafkajs/confluent-schema-registry';
import { Controller, Logger, OnModuleDestroy, OnModuleInit } from '@nestjs/common';
import { MessageService } from './message.service';
import { DomainEvent } from './event';
import { Consumer, Kafka, logCreator, LogEntry, logLevel } from 'kafkajs';
import { KAFKA_TOPIC_EVENT_MAP } from './message.config';

const kafkaLogger: logCreator = (level: logLevel) => {
  const logger = new Logger('KafkaJS'); // context label

  return ({ namespace, level, label, log }: LogEntry) => {
    const { message, ...extra } = log;

    switch (level) {
      case logLevel.ERROR:
        logger.error(`[${namespace}] ${message}`, extra);
        break;
      case logLevel.WARN:
        logger.warn(`[${namespace}] ${message}`, extra);
        break;
      case logLevel.INFO:
        logger.log(`[${namespace}] ${message}`, extra);
        break;
      case logLevel.DEBUG:
        logger.debug?.(`[${namespace}] ${message} ${JSON.stringify(extra)}`);
        break;
      default:
        logger.verbose?.(`[${namespace}] ${message} ${JSON.stringify(extra)}`);
    }
  };
};

@Controller()
export class KafkaMessageConsumer implements OnModuleInit, OnModuleDestroy {
  private readonly logger = new Logger(KafkaMessageConsumer.name);
  registry: SchemaRegistry;
  kafkaConsumer: Consumer;

  constructor(
    private messageService: MessageService,
  ) {
    this.kafkaConsumer = new Kafka({
      clientId: 'tompang-carpool',
      brokers: ['localhost:9092'], // TODO move to config
      logCreator: kafkaLogger,
    }).consumer({ groupId: 'notification-service' });
    this.registry = new SchemaRegistry({ host: 'http://localhost:8081' });
  }

  private async handleEvent<T extends DomainEvent>(
    ctor: new (payload: any) => T,
    message: Buffer,
    timestamp: String,
  ): Promise<T> {
    const decoded = await this.registry.decode(message);
    const event = new ctor(decoded);
    const eventDate = new Date(Number(timestamp));
    this.messageService.createNotificationFromEvent(event, eventDate);
    return event;
  }

  async onModuleInit() {
    await this.kafkaConsumer.connect();
    await this.kafkaConsumer.subscribe({ topics: Array.from(KAFKA_TOPIC_EVENT_MAP.keys()), fromBeginning: true });
    await this.kafkaConsumer.run({
      eachMessage: async ({ topic, partition, message }) => {
        const eventCtor = KAFKA_TOPIC_EVENT_MAP.get(topic);
        if (eventCtor !== undefined && message.value !== null) {
          this.logger.log(
            `Received message on topic "${topic}" (partition ${partition}, offset ${message.offset}, timestamp ${message.timestamp})`
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
    await this.kafkaConsumer.disconnect();
  }
}

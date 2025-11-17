import { SchemaRegistry } from "@kafkajs/confluent-schema-registry";
import { Controller, Inject, Logger } from "@nestjs/common";
import { EventPattern } from "@nestjs/microservices";
import KafkaTopics from "src/common/kafka-topics";
import { EventTranslatorService } from "./event-translator.service";
import { SocketEventGateway } from "./socket-event.gateway";
import connectionConfig, { ConnectionConfig } from "src/config/connection.config";

@Controller()
export class EventConsumerController {
  
  private readonly logger = new Logger(EventConsumerController.name);

  registry: SchemaRegistry
  constructor(
    private eventTranslatorService: EventTranslatorService,
    private eventGateway: SocketEventGateway,
    @Inject(connectionConfig.KEY) config: ConnectionConfig,
  ) {
    this.registry = new SchemaRegistry({ host: config.schemaRegistryUrl });
  }

  @EventPattern(KafkaTopics.Chat.CHAT_MESSAGE_SENT)
  async handleChatMessageSent(message: Buffer) {
    const decoded = await this.registry.decode(message);
    this.logger.log(KafkaTopics.Chat.CHAT_MESSAGE_SENT, decoded);
    const socketEvents = this.eventTranslatorService.translateChatMessageSent(decoded);
    for (const socketEventDto of socketEvents) {
      try {
        this.eventGateway.sendSocketEvent(socketEventDto);
      } catch (exception) {
        console.log(exception);
      }
    }
  }

  @EventPattern(KafkaTopics.Notification.NOTIFICATION_RECEIVED)
  async handleNotificationReceived(message: Buffer) {
    const decoded = await this.registry.decode(message);
    this.logger.log(KafkaTopics.Notification.NOTIFICATION_RECEIVED, decoded);
    this.eventGateway.sendSocketEvent(this.eventTranslatorService.translateNotificationReceived(decoded));
  }
}
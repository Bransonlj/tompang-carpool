import { SchemaRegistry } from "@kafkajs/confluent-schema-registry";
import { Controller, Logger } from "@nestjs/common";
import { EventPattern } from "@nestjs/microservices";
import KafkaTopics from "src/common/kafka-topics";
import { EventTranslatorService } from "./event-translator.service";
import { SocketEventGateway } from "./socket-event.gateway";

@Controller()
export class EventConsumerController {
  
  private readonly logger = new Logger(EventConsumerController.name);

  registry: SchemaRegistry
  constructor(
    private eventTranslatorService: EventTranslatorService,
    private eventGateway: SocketEventGateway,
  ) {
    this.registry = new SchemaRegistry({ host: 'http://localhost:8081' }); // TODO move to config
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
}
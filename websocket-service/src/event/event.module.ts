import { Module } from '@nestjs/common';
import { EventTranslatorService } from './event-translator.service';
import { EventConsumerController } from './event-consumer.controller';
import { SocketEventGateway } from './socket-event.gateway';

@Module({
  providers: [
    EventTranslatorService,
    SocketEventGateway,
  ],
  controllers: [
    EventConsumerController,
  ],
})
export class EventModule {}

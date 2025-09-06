import { Injectable } from "@nestjs/common";
import { ChatMessageSentEvent } from "src/common/events/chat-message-sent.event";
import { SendSocketEventDto, SocketEventName } from "./dto/send-socket-event.dto";

@Injectable()
export class EventTranslatorService {
  translateChatMessageSent(payload: ChatMessageSentEvent): SendSocketEventDto[] {
    return payload.groupChatUsers.map<SendSocketEventDto>(groupUserId => ({
      eventName: SocketEventName.CHAT_MESSAGE,
      targetUserId: groupUserId,
      payload: {
        messageId: payload.messageId,
        groupChatId: payload.groupChatId,
        senderUserId: payload.senderUserId,
        createdAt: payload.createdAt,
        message: payload.message,
      },
    }));
  }
}
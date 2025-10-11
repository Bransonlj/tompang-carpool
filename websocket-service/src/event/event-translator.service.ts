import { Injectable } from "@nestjs/common";
import { SendSocketEventDto, SocketEventName } from "./dto/send-socket-event.dto";
import { ChatMessageSentEvent, NotificationReceivedEvent } from "src/common/events";

@Injectable()
export class EventTranslatorService {

  /**
   * Creates a socket event dto for each user in the group, except the sender
   * @param payload 
   * @returns 
   */
  translateChatMessageSent(payload: ChatMessageSentEvent): SendSocketEventDto[] {
    return payload.groupChatUsers.filter(groupUserId => groupUserId !== payload.senderUserId)
      .map<SendSocketEventDto>(groupUserId => ({
        eventName: SocketEventName.CHAT_MESSAGE,
        targetUserId: groupUserId,
        payload: {
          messageId: payload.messageId,
          groupChatId: payload.groupChatId,
          senderUserId: payload.senderUserId,
          createdAt: new Date(payload.createdAt),
          message: payload.message,
        },
      }));
  }

  translateNotificationReceived(payload: NotificationReceivedEvent): SendSocketEventDto {
    return {
      eventName: SocketEventName.NOTIFICATION,
      targetUserId: payload.userId,
      payload: {
        notificationId: payload.notificationId,
        createdAt: new Date(payload.createdAt),
        message: payload.message,
      },
    }
  }
}
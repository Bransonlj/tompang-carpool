import { Injectable } from '@nestjs/common';
import { NotificationService } from 'src/notification/notification.service';
import { DomainEvent } from './event';


@Injectable()
export class MessageService {
  constructor(
    private notificationService: NotificationService,
  ) {}

  async createNotificationFromEvent(event: DomainEvent, timestamp: Date) {
    
    this.notificationService.createNotification({
      userId: event.getTargetUserId(),
      createdAt: timestamp,
      message: event.getMessage(),
    })
  }
}

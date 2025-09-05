import { Injectable } from '@nestjs/common';
import { NotificationGateway } from './notification.gateway';
import { NotificationRepository } from './notification.repository';
import { CreateUserNotificationDto } from './dto/user-notification';

@Injectable()
export class NotificationService {
  constructor(
    private gateway: NotificationGateway,
    private repository: NotificationRepository,
  ) {}

  async createNotification(createDto: CreateUserNotificationDto) {
    const createdNotification = await this.repository.saveNotification(createDto);
    await this.gateway.sendNotification(createdNotification);
    return createdNotification;
  }

  async getNotificationsByUser(userId: string) {
    return await this.repository.getUserNotifications(userId);
  }


}

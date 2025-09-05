import { Body, Controller, Get, Param, Post } from '@nestjs/common';
import { NotificationService } from './notification.service';
import { CreateUserNotificationDto } from './dto/user-notification';

@Controller('/api/notification')
export class NotificationController {

  constructor(
    private notificationService: NotificationService,
  ) {}

  @Post("create/")
  async createNotification(@Body() notification: CreateUserNotificationDto) {
    await this.notificationService.createNotification(notification);
  }

  @Get("user/:id")
  async testCass(@Param('id') userId: string) {
    const notifications = await this.notificationService.getNotificationsByUser(userId);
    return notifications;
  }

}

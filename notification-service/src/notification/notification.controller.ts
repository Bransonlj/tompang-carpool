import { Body, Controller, Get, Param, Post } from '@nestjs/common';
import { NotificationService } from './notification.service';
import { CreateUserNotificationDto, UserNotificationDto } from './dto/user-notification';

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
  async getNotificationsByUserId(@Param('id') userId: string): Promise<UserNotificationDto[]> {
    return await this.notificationService.getNotificationsByUser(userId);;
  }

}

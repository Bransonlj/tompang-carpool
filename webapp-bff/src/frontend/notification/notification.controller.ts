import { Controller, Get, Param } from '@nestjs/common';
import { UserNotificationDto } from './dto';

@Controller('api/notification')
export class NotificationController {

  @Get("user/:uid")
  async getUserNotifications(@Param("uid") userId: string): Promise<UserNotificationDto[]> {
    return [
      {
        notificationId: "1",
        userId: "test-user-id",
        createdAt: new Date(),
        message: "a notification has been notified",
      },
      {
        notificationId: "2",
        userId: "test-user-id",
        createdAt: new Date(),
        message: "another notification has been notified again",
      },
      {
        notificationId: "3",
        userId: "test-user-id",
        createdAt: new Date(),
        message: "test notification",
      },
    ]
  }

}

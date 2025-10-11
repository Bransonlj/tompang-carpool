import { Controller, Get, Headers, Param } from '@nestjs/common';
import { NotificationService } from 'src/backend/notification/notification.service';
import { NotificationDto } from 'src/backend/notification/dto';

@Controller('api/notification')
export class NotificationController {
  constructor(
    private notificationService: NotificationService,
  ) {}

  @Get("user/:uid")
  async getUserNotifications(@Param("uid") userId: string, @Headers("Authorization") authHeader: string): Promise<NotificationDto[]> {
    return this.notificationService.getNotificationsByUserId(userId, authHeader);
  }

}

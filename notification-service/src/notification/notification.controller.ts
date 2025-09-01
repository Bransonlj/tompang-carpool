import { Controller, Param, Post } from '@nestjs/common';
import { NotificationGateway } from './notification.gateway';
import { NotificationService } from './notification.service';

@Controller('/api/notification')
export class NotificationController {

  constructor(
    private notificationGateway: NotificationGateway,
    private notificationService: NotificationService,
  ) {}

  @Post("create/:id")
  createNotification(@Param('id') userId: string) {
    this.notificationGateway.sendNotification({ title: "buh", message: "cuh cuh buh facuh"}, userId);
  }

  @Post("cass")
  async testCass() {
    await this.notificationService.testInsert();
    return "buh"
  }

}

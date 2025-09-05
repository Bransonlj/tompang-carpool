import { Injectable } from '@nestjs/common';
import cassandra, { Client } from "cassandra-driver";
import { CreateUserNotificationDto, UserNotificationDto } from './dto/user-notification';
import { randomUUID } from 'crypto';

@Injectable()
export class NotificationRepository {

  client: Client;
  
  public get notificationKeySpace() : string {
    return "notifications"
  }
  
  
  public get notificationTable() : string {
    return "user_notifications";
  }
  
  constructor() {
    // TODO move to config
    this.client = new Client({
      contactPoints: ['127.0.0.1'], // or 'localhost'
      localDataCenter: 'datacenter1', // default DC in cassandra:4.1
      keyspace: this.notificationKeySpace,       // use your keyspace name
    })
  }

  async saveNotification(notification: CreateUserNotificationDto): Promise<UserNotificationDto> {
    const id = randomUUID();
    const query = `INSERT INTO ${this.notificationKeySpace}.${this.notificationTable} (user_id, created_at, notification_id, message) VALUES (?, ?, ?, ?)`;
    await this.client.execute(query, 
      [notification.userId, notification.createdAt, id, notification.message], { prepare: true });
    return {
      notificationId: id,
      ...notification,
    }
  }

  async getUserNotifications(userId: string): Promise<UserNotificationDto[]> {
    const query = `SELECT * FROM ${this.notificationKeySpace}.${this.notificationTable}
                    WHERE user_id = ?
                    LIMIT 10;`
    const result = await this.client.execute(query, [userId], { prepare: true });
    return result.rows.map(row => ({
      userId: row.user_id,
      createdAt: row.created_at,
      notificationId: row.notification_id,
      message: row.message,
    }));
  }

}

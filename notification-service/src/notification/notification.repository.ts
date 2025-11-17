import { Inject, Injectable } from '@nestjs/common';
import cassandra, { Client } from "cassandra-driver";
import { CreateUserNotificationDto, UserNotificationDto } from './dto/user-notification';
import { randomUUID } from 'crypto';
import connectionConfig, { ConnectionConfig } from 'src/config/connection.config';

@Injectable()
export class NotificationRepository {

  client: Client;
  
  public get notificationKeySpace() : string {
    return "notifications"
  }
  
  
  public get notificationTable() : string {
    return "user_notifications";
  }
  
  constructor(
    @Inject(connectionConfig.KEY) config: ConnectionConfig,
  ) {
    this.client = new Client({
      contactPoints: config.cassandraContactPoints,
      protocolOptions: {
        port: config.cassandraPort,
      },
      localDataCenter: 'datacenter1', // default DC in cassandra:4.1
      keyspace: this.notificationKeySpace,
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

  async getNotificationsByUserSorted(userId: string): Promise<UserNotificationDto[]> {
    const query = `SELECT * FROM ${this.notificationKeySpace}.${this.notificationTable}
                    WHERE user_id = ?
                    ORDER BY created_at DESC;`
    const result = await this.client.execute(query, [userId], { prepare: true });
    return result.rows.map(row => ({
      userId: row.user_id,
      createdAt: row.created_at,
      notificationId: row.notification_id,
      message: row.message,
    }));
  }

}

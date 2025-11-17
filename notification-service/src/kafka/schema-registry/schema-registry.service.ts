import { readAVSC, SchemaRegistry } from '@kafkajs/confluent-schema-registry';
import { Inject, Injectable } from '@nestjs/common';
import { KafkaTopic } from '../topics';
import * as path from 'path';
import connectionConfig, { ConnectionConfig } from 'src/config/connection.config';

@Injectable()
export class SchemaRegistryService {
  readonly registry: SchemaRegistry;
  constructor(
    @Inject(connectionConfig.KEY) config: ConnectionConfig
  ) {
    this.registry = new SchemaRegistry({ host: config.schemaRegistryUrl });
  }

  public async loadTopicSchema(topic: KafkaTopic) {
    switch (topic) {
      case KafkaTopic.NOTIFICATION_RECEIVED: {
        const schemaPath = path.resolve(__dirname, '../../../../schemas/event/notification/NotificationReceivedEvent.avsc');
        const schema = readAVSC(schemaPath);

        const { id } = await this.registry.register(schema, { subject: 'notification-received-value' });
        return id;
      }    
      default:
        throw Error(`Topic schema not registered: ${topic}`);
    }

  }
}

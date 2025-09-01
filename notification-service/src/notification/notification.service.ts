import { Injectable } from '@nestjs/common';
import cassandra, { Client } from "cassandra-driver";

@Injectable()
export class NotificationService {

  client: Client;

  constructor() {
    this.client = new Client({
      contactPoints: ['127.0.0.1'], // or 'localhost'
      localDataCenter: 'datacenter1', // default DC in cassandra:4.1
      keyspace: 'store',       // use your keyspace name
    })
  }

  async testInsert() {
    const query = 
    "INSERT INTO store.shopping_cart (userid, item_count, last_update_timestamp) VALUES ('2222', 2, toTimeStamp(now()));"
    await this.client.execute(query);
    console.log("added")
  }
}

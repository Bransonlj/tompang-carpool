import { Injectable, OnModuleDestroy, OnModuleInit } from '@nestjs/common';
import kafka from '../kafka';
import { Producer } from 'kafkajs';

@Injectable()
export class KafkaProducerService implements OnModuleInit, OnModuleDestroy  {
  
  readonly producer: Producer;
  constructor() {
    this.producer = kafka.producer();
  }

  async onModuleInit() {
    await this.producer.connect();
  }
  
  async onModuleDestroy() {
    await this.producer.disconnect();
  }
}

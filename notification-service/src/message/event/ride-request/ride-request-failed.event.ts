import { DomainEvent } from "..";

export class RideRequestFailedEvent implements DomainEvent {
  requestId: string;
  reason: string;
  riderId: string;

  constructor(message: any) {
    this.requestId = message.requestId;
    this.reason = message.reason;
    this.riderId = message.riderId;
  }

  getMessage(): string {
    return `A ride request has failed: ${this.reason}`;
  }
  getTargetUserId(): string {
    return this.riderId;
  }
}
import { RideRequestFailedEvent } from "src/kafka/event/ride-request";
import { DomainEvent } from "..";

export class RideRequestFailedDomainEvent implements DomainEvent {
  event: RideRequestFailedEvent;

  constructor(event: RideRequestFailedEvent) {
    this.event = event;
  }

  getMessage(): string {
    return `A ride request has failed: ${this.event.reason}`;
  }
  getTargetUserId(): string {
    return this.event.riderId;
  }
}
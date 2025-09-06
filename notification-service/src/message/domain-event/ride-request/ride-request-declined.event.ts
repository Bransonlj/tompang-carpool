import { RideRequestDeclinedEvent } from "src/kafka/event/ride-request";
import { DomainEvent } from "..";

export class RideRequestDeclinedDomainEvent implements DomainEvent {

  event: RideRequestDeclinedEvent
  constructor(event: RideRequestDeclinedEvent) {
    this.event = event;
  }

  getMessage(): string {
    return `A carpool has declined your ride request`;
  }
  getTargetUserId(): string {
    return this.event.riderId;
  }
}
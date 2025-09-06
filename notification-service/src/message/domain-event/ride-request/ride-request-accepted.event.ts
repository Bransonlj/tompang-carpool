import { RideRequestAcceptedEvent } from "src/kafka/event/ride-request";
import { DomainEvent } from "..";

export class RideRequestAcceptedDomainEvent implements DomainEvent {
  event: RideRequestAcceptedEvent;
  constructor(event: RideRequestAcceptedEvent) {
    this.event = event;
  }

  getMessage(): string {
    return `A carpool has accepted your ride request`;
  }
  getTargetUserId(): string {
    return this.event.riderId;
  }
}
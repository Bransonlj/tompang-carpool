import { RideRequestMatchedEvent } from "src/kafka/event/ride-request";
import { DomainEvent } from "..";

export class RideRequestMatchedDomainEvent implements DomainEvent {
  event: RideRequestMatchedEvent;

  constructor(event: RideRequestMatchedEvent) {
    this.event = event;
  }

  getMessage(): string {
    return `${this.event.matchedCarpoolIds.length} matching carpools found for your ride request`;
  }
  getTargetUserId(): string {
    return this.event.riderId;
  }
}
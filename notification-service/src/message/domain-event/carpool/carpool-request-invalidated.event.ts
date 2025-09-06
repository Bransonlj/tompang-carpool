import { CarpoolRequestInvalidatedEvent } from "src/kafka/event/carpool";
import { DomainEvent } from "..";

export class CarpoolRequestInvalidatedDomainEvent implements DomainEvent {
  event: CarpoolRequestInvalidatedEvent

  constructor(event: CarpoolRequestInvalidatedEvent) {
    this.event = event;
  }

  getMessage(): string {
    return `Rider request to your carpool has expired: ${this.event.reason}`;
  }
  getTargetUserId(): string {
    return this.event.driverId;
  }
  
}
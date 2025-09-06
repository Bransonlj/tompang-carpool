import { CarpoolMatchedEvent } from "src/kafka/event/carpool";
import { DomainEvent } from "..";

export class CarpoolMatchedDomainEvent implements DomainEvent {

  event: CarpoolMatchedEvent

  constructor(event: CarpoolMatchedEvent) {
    this.event = event;
  }

  getMessage(): string {
    return `A rider has request to join your carpool`;
  }
  getTargetUserId(): string {
    return this.event.driverId;
  }
  
}
import { DriverRegistrationApprovedEvent } from "src/kafka/event/driver";
import { DomainEvent } from "..";

export class DriverRegistrationApprovedDomainEvent implements DomainEvent {
  event: DriverRegistrationApprovedEvent
  constructor(event: DriverRegistrationApprovedEvent) {
    this.event = event;
  }

  getMessage(): string {
    return `Your driver registration has been approved`;
  }
  getTargetUserId(): string {
    return this.event.userId;
  }
  
}
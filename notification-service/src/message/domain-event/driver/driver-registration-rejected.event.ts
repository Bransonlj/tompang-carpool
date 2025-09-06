import { DriverRegistrationRejectedEvent } from "src/kafka/event/driver";
import { DomainEvent } from "..";

export class DriverRegistrationRejectedDomainEvent implements DomainEvent {
  event: DriverRegistrationRejectedEvent

  constructor(event: DriverRegistrationRejectedEvent) {
    this.event = event;
  }

  getMessage(): string {
    return `Your driver registration has been rejected`;
  }
  getTargetUserId(): string {
    return this.event.userId;
  }
  
}
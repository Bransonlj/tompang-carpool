import { DomainEvent } from "..";

export class DriverRegistrationApprovedEvent implements DomainEvent {
  driverRegistrationId: string;
  userId: string;

  constructor(message: any) {
    this.driverRegistrationId = message.driverRegistrationId;
    this.userId = message.userId;
  }

  getMessage(): string {
    return `Your driver registration has been approved`;
  }
  getTargetUserId(): string {
    return this.userId;
  }
  
}
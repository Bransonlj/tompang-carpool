import { DomainEvent } from "..";

export class DriverRegistrationRejectedEvent implements DomainEvent {
  driverRegistrationId: string;
  userId: string;

  constructor(message: any) {
    this.driverRegistrationId = message.driverRegistrationId;
    this.userId = message.userId;
  }

  getMessage(): string {
    return `Your driver registration has been rejected`;
  }
  getTargetUserId(): string {
    return this.userId;
  }
  
}
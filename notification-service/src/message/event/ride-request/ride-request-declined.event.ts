import { DomainEvent } from "..";

export class RideRequestDeclinedEvent implements DomainEvent {
  requestId: string;
  carpoolId: string;
  riderId: string;

  constructor(message: any) {
    this.requestId = message.requestId;
    this.carpoolId = message.carpoolId;
    this.riderId = message.riderId;
  }

  getMessage(): string {
    return `A carpool has declined your ride request`;
  }
  getTargetUserId(): string {
    return this.riderId;
  }
}
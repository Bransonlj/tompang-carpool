import { DomainEvent } from "..";

export class RideRequestMatchedEvent implements DomainEvent {
  requestId: string;
  matchedCarpoolIds: string[];
  riderId: string;

  constructor(message: any) {
    this.requestId = message.requestId;
    this.matchedCarpoolIds = message.matchedCarpoolIds;
    this.riderId = message.riderId;
  }

  getMessage(): string {
    return `${this.matchedCarpoolIds.length} matching carpools found for your ride request`;
  }
  getTargetUserId(): string {
    return this.riderId;
  }
}
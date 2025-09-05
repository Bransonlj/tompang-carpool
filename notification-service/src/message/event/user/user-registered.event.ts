import { DomainEvent } from "..";

export class UserRegisteredEvent implements DomainEvent {

  userId: string;
  firstName: string;
  lastName: string;


  constructor(message: any) {
    this.userId = message.userId;
    this.firstName = message.firstName;
    this.lastName = message.lastName;
  }

  getTargetUserId(): string {
      return this.userId;
  }

  getMessage(): string {
      return `User registered: ${this.firstName} ${this.lastName}`;
  }
}
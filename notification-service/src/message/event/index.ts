export interface DomainEvent {
  getMessage(): string;
  getTargetUserId(): string;
}
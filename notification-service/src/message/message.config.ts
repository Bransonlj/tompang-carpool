import { DomainEvent } from "src/message/event";
import { UserRegisteredEvent } from "src/message/event/user";

export const KAFKA_TOPIC_EVENT_MAP: ReadonlyMap<string, new (payload: any) => DomainEvent> = new Map([
  ["user-registered", UserRegisteredEvent],
]);
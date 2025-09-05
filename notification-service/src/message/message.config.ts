import { DomainEvent } from "src/message/event";
import { UserRegisteredEvent } from "src/message/event/user";
import { CarpoolMatchedEvent, CarpoolRequestInvalidatedEvent } from "./event/carpool";
import { RideRequestAcceptedEvent, RideRequestDeclinedEvent, RideRequestFailedEvent, RideRequestMatchedEvent } from "./event/ride-request";

enum Topic {
  USER_REGISTERED="user-registered",
  
  CARPOOL_CREATED = "carpool-created",
  CARPOOL_MATCHED = "carpool-matched",
  CARPOOL_REQUEST_ACCEPTED = "carpool-request-accepted",
  CARPOOL_REQUEST_DECLINED = "carpool-request-declined",
  CARPOOL_REQUEST_INVALIDATED = "carpool-request-invalidated",

  REQUEST_CREATED = "ride-request-created",
  REQUEST_MATCHED = "ride-request-matched",
  REQUEST_FAILED = "ride-request-failed",
  REQUEST_ACCEPTED = "ride-request-accepted",
  REQUEST_DECLINED = "ride-request-declined",
}

type DomainEventConstructor<T extends DomainEvent = DomainEvent> = new (payload: any) => T;

export const KAFKA_TOPIC_EVENT_MAP: ReadonlyMap<string, DomainEventConstructor> = new Map<string, DomainEventConstructor>([
  // user events
  [Topic.USER_REGISTERED, UserRegisteredEvent],

  // carpool events
  [Topic.CARPOOL_MATCHED, CarpoolMatchedEvent],
  [Topic.CARPOOL_REQUEST_INVALIDATED, CarpoolRequestInvalidatedEvent],

  // ride-request events
  [Topic.REQUEST_ACCEPTED, RideRequestAcceptedEvent],
  [Topic.REQUEST_DECLINED, RideRequestDeclinedEvent],
  [Topic.REQUEST_FAILED, RideRequestFailedEvent],
  [Topic.REQUEST_MATCHED, RideRequestMatchedEvent],
]);
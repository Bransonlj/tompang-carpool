import { DomainEvent } from "src/message/domain-event";
import { CarpoolMatchedDomainEvent, CarpoolRequestInvalidatedDomainEvent } from "./domain-event/carpool";
import { RideRequestAcceptedDomainEvent, RideRequestDeclinedDomainEvent, RideRequestFailedDomainEvent, RideRequestMatchedDomainEvent } from "./domain-event/ride-request";
import { DriverRegistrationApprovedDomainEvent, DriverRegistrationRejectedDomainEvent } from "./domain-event/driver";
import { KafkaTopic } from "src/kafka/topics";

type DomainEventConstructor<T extends DomainEvent = DomainEvent> = new (payload: any) => T;

export const KAFKA_TOPIC_EVENT_MAP: ReadonlyMap<string, DomainEventConstructor> = new Map<string, DomainEventConstructor>([
  // user events

  // driver events
  [KafkaTopic.DRIVER_REGISTRATION_APPROVED, DriverRegistrationApprovedDomainEvent],
  [KafkaTopic.DRIVER_REGISTRATION_REJECTED, DriverRegistrationRejectedDomainEvent],

  // carpool events
  [KafkaTopic.CARPOOL_MATCHED, CarpoolMatchedDomainEvent],
  [KafkaTopic.CARPOOL_REQUEST_INVALIDATED, CarpoolRequestInvalidatedDomainEvent],

  // ride-request events
  [KafkaTopic.REQUEST_ACCEPTED, RideRequestAcceptedDomainEvent],
  [KafkaTopic.REQUEST_DECLINED, RideRequestDeclinedDomainEvent],
  [KafkaTopic.REQUEST_FAILED, RideRequestFailedDomainEvent],
  [KafkaTopic.REQUEST_MATCHED, RideRequestMatchedDomainEvent],
]);
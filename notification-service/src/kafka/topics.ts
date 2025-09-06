export enum KafkaTopic {
  USER_REGISTERED="user-registered",
  
  DRIVER_REGISTRATION_APPROVED = "driver-registration-approved",
  DRIVER_REGISTRATION_REJECTED = "driver-registration-rejected",
  DRIVER_DEREGISTERED = "driver-deregistered",

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

  NOTIFICATION_RECEIVED = "notification-received",
}
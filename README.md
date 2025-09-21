# Tompang Carpool

Tompang Carpool is a carpooling app, where drivers are matched with riders on similar routes to carpool together, reducing carbon emissions and saving the environment!

## Architecture

![Tompang Carpool Architecture Diagram](./docs/diagrams/TompangCarpoolArchitecture.png)

Tompang is built with a event-driven, microservices architecture, built with following technologies.

#### Frameworks

* **Spring Boot**: Most backend services.
* **NestJS**: Some backend services.
* **React**: Webapp client.

#### Communication

* **Kafka**: Primary message broker for event-driven communication between services.
* **RabbitMQ**: Queue for jobs like geocoding jobs or driver verification.
* **Spring Cloud Gateway**: API gateway.
* **Socket.IO**: Websocket communcation between servers & clients.

#### Databases

* **PostgreSQL**: Standard relational database for most services.
* **KurrentIO** (formally known as EventStoreDB): Event native database/eventstore for services using event sourcing(eg. Carpool service).
* **Cassandra**: Distributed database for services with high read & write (eg. Notification & Chat services).
* **Redis**: Lightweight caching.

#### Misc

* **Avro**: Schema generation and event serialization & deserialization for Kafka topics with the Schema-Registry.
* **JUnit + Mockito**: Spring unit tests.

# Services/Components

## API Gateway

The API gateway service acts as the HTTP & Websocket entrypoint for the backend services, using the Spring Cloud Gateway framework. It also handles authentication and authorization of all incoming requests using JWTokens carrying userId and roles credentials, then attaching `X-User-Id` and `X-User-Roles` to all authenticated & authorized incoming requests.

## Carpool Service

The carpool service is responsible for managing Carpools (created by drivers) and Ride Requests (created by riders) and matchmaking of Carpools and Ride Requests. It uses the CQRS pattern along with Event-Sourcing to have a auditable log of events.

![Carpool Service UML Diagram](./docs/diagrams/CarpoolServiceArchitecture.png)

In general, any commands invoked on the command side will raise an event, which is published on kafka and appended to the event store KurrentIO. The event store stream it is appended to correspond to the aggregate and the aggregate id(eg. carpool_a12b-23xv-234x-asdb, ride-request_bg76-898s-sd87-kjh3). The event store supports concurrent operations by verifying the latest version number of the stream when appending events, rejecting the append operation if the version number is outdated.

The projectors on the query side will consume the kafka event if relevant, and update the query specific database view of the entity on PostgreSQL. For some complex operations, the command-side `ProcessManager`s might access the query-side database view, which is only eventually consistent. However for most commands, the `CommandHandler`s will rehydrate aggregates using eventsfrom the event store to perform validations, which has strong consistency.

The following diagrams demonstrate the sequence of events for some commands when invoked.

### Create Carpool Command

![Create Carpool Sequence Diagram](./docs/diagrams/carpool-service/CreateCarpoolSequence.png)

When the `CreateCarpoolCommand` is invoked, the command handler creates a `CarpoolAggregate` using the command. The aggregate raises the `CarpoolCreatedEvent` which is appended to the event repository and published to Kafka. On the Query side, the projector consumes this events and updates the query view database accordingly.

### Create Ride Request Command

![Create and Match RideRequest Sequence Diagram](./docs/diagrams/carpool-service/CreateAndMatchRideRequestSequence.png)

When the `CreateRideRequestCommand` is invoked, the command handler creates a `RideRequestAggregate` using the command. The aggregate raises the `RideRequestCreatedEvent` which is appended to the event repository and published to Kafka. On the Query side, the projector consumes this events and updates the query view database accordingly. The `RideRequestProcessManager` also consumes this event and is responsible for matching the ride request with suitable carpools after it is created. It queries the `CarpoolQueryService` on the query side to find matching carpools based on  the ride request details like timerange & route. If no matching carpools are found, the ride request is marked as failed by invoking the `FailRideRequestCommand`. Else, for each matching carpool, the `MatchCarpoolCommand` is invoked between that carpool and the ride request. Finally the `MatchRideRequestCommand` is also invoked for the ride request.

### Handle MatchCarpoolCommand

![Handle Carpool Matched Sequence Diagram](./docs/diagrams/carpool-service/HandleCarpoolMatchedSequence.png)

`RideRequestProcessManager` invokes the `MatchCarpoolCommand` which contains:
* `requestId`
* `carpoolId`

The command handler fetches the list of past events for that carpool aggregate from the event repository. It creates the `CarpoolAggregate` by rehydrating it from the event history and invokes the command on the aggregate. The aggregate raises the `CarpoolMatchedEvent` which is appended to the event repository and published to Kafka. On the query side, the `MatchProjector` consumes the event and updates the Carpool and RideRequest entities by matching them together.

### Handle MatchRideRequestCommand

![Handle Match Ride Request Sequence Diagram](./docs/diagrams/carpool-service/HandleMatchRideRequestSequence.png)

`RideRequestProcessManager` invokes the `MatchRideRequestCommand` which contains:
* `requestId`
* `matchedCarpoolIds`

The command handler fetches the list of past events for that ride request id from the event repository. It creates the `RideRequestAggregate` by rehydrating it from the event history and invokes the command on the aggregate. The aggregate raises the `RideRequestMatchedEvent` which is appended to the event repository and published to Kafka. On the query side, the `MatchProjector` consumes the event and fetches the RideRequest & all Carpools from `matchedCarpoolIds` entities and updates them by matching them together.

### Accept Carpool Request

![Accept Carpool Request Sequence Diagram](./docs/diagrams/carpool-service/AcceptCarpoolRequestSequence.png)

`AcceptCarpoolRequestCommand` contains:
* `requestId`
* `carpoolId`
* `leftoverCarpoolIds`: ids of all other carpools in the request was matched to.

When invoked by the command controller, the command handler rehydrates the `CarpoolAggregate` and `RideRequestAggregate` from event history of fetched from the event repository for those `requestId` and `carpoolId` streams. Then it checks if the `RideRequestAggregate` can be assigned to a carpool, throwing a `BadRequestException` if it can't be assigned. Else, the command is invoked on both aggregates, raising the `CarpoolRequestAcceptedEvent` and `RideRequestAcceptedEvent` events, appending to the event repository and publishing to Kafka.

`MatchProjector` on the Query side consumes the `RideRequestAcceptedEvent` event and updates the RideRequest by removing it from all of its matchedCarpools' pendingRideRequests list. Then it assigns the RideRequest and Carpool together.

`RideRequestProcessManager` also consumes the `RideRequestAcceptedEvent` event and invokes the `InvalidateCarpoolRequestCommand` to all carpools in `leftoverCarpoolIds`, as the riderequest is already assigned, so all the requests made to these carpools are now invalid.

### Handle InvalidateCarpoolRequestCommand

![Invalidate Carpool Request Sequence Diagram](./docs/diagrams/carpool-service/InvalidateCarpoolRequestSequence.png)

`InvalidateCarpoolRequestCommand` contains:
* `carpoolId`
* `requestId`
* `reason`

When invoked by the `RideRequestProcessManager`, the command handler fetches the list of past events for that carpool id from the event repository. It creates the `CarpoolAggregate` by rehydrating it from the event history and invokes the command on the aggregate. The aggregate raises the `CarpoolRequestInvalidatedEvent` which is appended to the event repository and published to Kafka. On the query side, the `MatchProjector` consumes the event and fetches the RideRequest Carpool entities and updates them by removing them from each other's matched/pending lists.

### Decline Carpool Request

![Decline Carpool Request Sequence Diagram](./docs/diagrams/carpool-service/DeclineCarpoolRequestSequence.png)

`DeclineCarpoolRequestCommand` contains:
* `carpoolId`
* `requestId`

When invoked by the controller, the command handler rehydrates the `CarpoolAggregate` and `RideRequestAggregate` from event history of fetched from the event repository for those `requestId` and `carpoolId` streams. Then it invokes the command on both aggregates, raising the `CarpoolRequestDeclinedEvent` & `RideRequestDeclinedEvent` events, appending to the event repository and kafka.

The `MatchProjector` in the Query side consumes `RideRequestDeclinedEvent` and update the Carpool and RideRequest entities in the query view database by removing them from each other's matched/pending lists.

`RideRequestProcessManager` also consumes `RideRequestDeclinedEvent` and rehydrates the `RideRequestAggregate` with the event history from event repository. Then it checks if there are still any carpools matched to the ride request. If there are no more pending carpool matches, it invokes the `FailRideRequstCommand`.

### Handle FailRideRequestCommand

![Fail RideRequest Request Sequence Diagram](./docs/diagrams/carpool-service/FailRideRequestSequence.png)

`FailRideRequestCommand` contains:
* `requestId`
* `reason`

When invoked by `RideRequestProcessManager`, the command handler rehydrates the `RideRequestAggregate` with the event history from event repository. Then it invokes the command on the aggregate which raises the `RideRequestFailedEvent`, appending it to the event repository and publishing to Kafka.

On the Query side, `RideRequestProjector` consumes `RideRequestFailedEvent` and updates the RideRequest entity by setting its status to `FAILED`.

## User Service

The user service is responsible for manager users and their profiles, and also registration and logging in of users, generating their JWToken carrying the user's Id and Roles credentials. 

# Resources

* Diagrams: [sequencediagram.org](https://sequencediagram.org/), [draw.io](https://draw.io)
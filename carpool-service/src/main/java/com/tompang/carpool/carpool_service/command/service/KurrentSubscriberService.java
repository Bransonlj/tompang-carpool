package com.tompang.carpool.carpool_service.command.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tompang.carpool.carpool_service.command.domain.DomainEvent;
import com.tompang.carpool.carpool_service.command.repository.EventRepository;
import com.tompang.carpool.carpool_service.command.repository.EventRepository.CarpoolConstants;
import com.tompang.carpool.carpool_service.command.repository.EventRepository.RideRequestConstants;

import io.kurrent.dbclient.CreatePersistentSubscriptionToAllOptions;
import io.kurrent.dbclient.KurrentDBPersistentSubscriptionsClient;
import io.kurrent.dbclient.NackAction;
import io.kurrent.dbclient.PersistentSubscription;
import io.kurrent.dbclient.PersistentSubscriptionListener;
import io.kurrent.dbclient.ResolvedEvent;
import io.kurrent.dbclient.SubscriptionFilter;

@Service
public class KurrentSubscriberService {
    public static final String SUBSCRIPTION_GROUP = "carpool-service-subscription-group";
    private final KurrentDBPersistentSubscriptionsClient persistentSubscriptionsClient;
    private final EventRepository eventRepository;
    private final KafkaProducerService kafkaProducerService;
    private final Logger logger = LoggerFactory.getLogger(KurrentSubscriberService.class);

    public KurrentSubscriberService(
        KurrentDBPersistentSubscriptionsClient persistentSubscriptionsClient, 
        EventRepository eventRepository,
        KafkaProducerService kafkaProducerService
    ) {
        this.persistentSubscriptionsClient = persistentSubscriptionsClient;
        this.eventRepository = eventRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Create a persistent subsction group to carpool and ride-request streams.
     */
    public void createSubscriptionStream() {
        if (persistentSubscriptionsClient.getInfoToAll(SUBSCRIPTION_GROUP).join().isPresent()) {
            logger.warn("Persistent Subscription group: " + SUBSCRIPTION_GROUP + " already exists, skipping creation.");
        } else {
            logger.warn("Persistent Subscription group: " + SUBSCRIPTION_GROUP + " not found, creating.");
            SubscriptionFilter filter = SubscriptionFilter.newBuilder()
                .withStreamNameRegularExpression("^" + CarpoolConstants.STREAM_PREFIX + "|^" + RideRequestConstants.STREAM_PREFIX)
                .build();

            persistentSubscriptionsClient.createToAll(
                SUBSCRIPTION_GROUP,
                CreatePersistentSubscriptionToAllOptions.get()
                    .filter(filter)
                    .fromEnd());
        }

    }

    /**
     * Create the subscription and listener which deserializes and publishes the event to kafka topic.
     */
    public void subscribe() {
        persistentSubscriptionsClient.subscribeToAll(
        SUBSCRIPTION_GROUP,
        new PersistentSubscriptionListener() {
            @Override
            public void onEvent(PersistentSubscription subscription, int retryCount, ResolvedEvent event) {
                try {
                    DomainEvent domainEvent = eventRepository.deserializeEvent(event);
                    logger.info("Received event "
                        + event.getOriginalEvent().getRevision()
                        + "@" + event.getOriginalEvent().getStreamId()
                        + ":" + event.getOriginalEvent().getEventType()
                        + "/" + domainEvent.topicName()
                        + " " + domainEvent.getEvent().toString());
                    
                    kafkaProducerService.publishDomainEvent(domainEvent, event.getOriginalEvent().getStreamId()); // use stream id as key
                    subscription.ack(event);
                }
                    catch (Exception ex) {
                        subscription.nack(NackAction.Park, ex.getMessage(), event);
                }
            }

            @Override
            public void onCancelled(PersistentSubscription subscription, Throwable exception) {
                if (exception == null) {
                    logger.info("Subscription is cancelled");
                    return;
                }

                logger.info("Subscription was dropped due to " + exception.getMessage());
            }
        });
    }
}

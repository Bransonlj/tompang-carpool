package com.tompang.carpool.carpool_service.command.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Setup the KurrentSubscriberService subscriptions on startup.
 */
@Component
public class KurrentSubscriberRunner implements CommandLineRunner {

    private final KurrentSubscriberService subscriberService;

    public KurrentSubscriberRunner(KurrentSubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @Override
    public void run(String... args) throws Exception {
        subscriberService.createSubscriptionStream();
        subscriberService.subscribe();
    }
}
package com.tompang.carpool.carpool_service.query.projector;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tompang.carpool.carpool_service.common.ExternalTopics;
import com.tompang.carpool.carpool_service.query.entity.Carpool;
import com.tompang.carpool.carpool_service.query.entity.EventualAddress;
import com.tompang.carpool.carpool_service.query.entity.EventualAddressStatus;
import com.tompang.carpool.carpool_service.query.entity.RideRequest;
import com.tompang.carpool.carpool_service.query.geocode.enums.GeocodeEntity;
import com.tompang.carpool.carpool_service.query.geocode.enums.GeocodeEntityField;
import com.tompang.carpool.carpool_service.query.geocode.event.ReverseGeocodeCompletedEvent;
import com.tompang.carpool.carpool_service.query.repository.CarpoolQueryRepository;
import com.tompang.carpool.carpool_service.query.repository.RideRequestQueryRepository;

/**
 * Updates repository with data from geocode events
 */
@Component
public class GeocodeProjector {
    private final CarpoolQueryRepository carpoolRepository;
    private final RideRequestQueryRepository rideRequestRepository;

    public GeocodeProjector(CarpoolQueryRepository carpoolRepository, RideRequestQueryRepository rideRequestRepository) {
        this.carpoolRepository = carpoolRepository;
        this.rideRequestRepository = rideRequestRepository;
    }

    @KafkaListener(topics = ExternalTopics.Geocode.REVERSE_GEOCODE_COMPLETED, groupId = "carpool-service-query")
    public void handleGeocodeReverseCompleted(ReverseGeocodeCompletedEvent event) {
        EventualAddress eventualAddress = EventualAddress.get();
        if (event.success) {
            eventualAddress.setStatus(EventualAddressStatus.RESOLVED);
            eventualAddress.setAddressString(event.address);
        } else {
            eventualAddress.setStatus(EventualAddressStatus.FAILED);
        }

        if (event.entity.equals(GeocodeEntity.CARPOOL)) {
            Carpool carpool = carpoolRepository.findById(event.entityId).orElseThrow();
            if (event.field.equals(GeocodeEntityField.ORIGIN)) {
                carpool.setOriginEventualAddress(eventualAddress);
            } else if (event.field.equals(GeocodeEntityField.DESTINATION)) {
                carpool.setDestinationEventualAddress(eventualAddress);
            }

            carpoolRepository.save(carpool);
        } else if (event.entity.equals(GeocodeEntity.RIDEREQUEST)) {
            RideRequest request = rideRequestRepository.findById(event.entityId).orElseThrow();
            if (event.field.equals(GeocodeEntityField.ORIGIN)) {
                request.setOriginEventualAddress(eventualAddress);
            } else if (event.field.equals(GeocodeEntityField.DESTINATION)) {
                request.setDestinationEventualAddress(eventualAddress);
            }

            rideRequestRepository.save(request);
        }
    }
}

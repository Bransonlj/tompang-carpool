package com.tompang.carpool.carpool_service.query.projector;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tompang.carpool.carpool_service.common.ExternalTopics;
import com.tompang.carpool.carpool_service.query.entity.Carpool;
import com.tompang.carpool.carpool_service.query.entity.EventualAddress;
import com.tompang.carpool.carpool_service.query.entity.EventualAddressStatus;
import com.tompang.carpool.carpool_service.query.entity.RideRequest;
import com.tompang.carpool.carpool_service.query.repository.CarpoolQueryRepository;
import com.tompang.carpool.carpool_service.query.repository.RideRequestQueryRepository;
import com.tompang.carpool.geospatial.ReverseGeocodeCompletedEvent;
import com.tompang.carpool.geospatial.enums.GeocodeEntity;
import com.tompang.carpool.geospatial.enums.GeocodeEntityField;

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
        if (event.getSuccess()) {
            eventualAddress.setStatus(EventualAddressStatus.RESOLVED);
            eventualAddress.setAddressString(event.getAddress());
        } else {
            eventualAddress.setStatus(EventualAddressStatus.FAILED);
        }

        if (event.getEntity().equals(GeocodeEntity.CARPOOL)) {
            Carpool carpool = carpoolRepository.findById(event.getEntityId()).orElseThrow();
            if (event.getField().equals(GeocodeEntityField.ORIGIN)) {
                carpool.setOriginEventualAddress(eventualAddress);
            } else if (event.getField().equals(GeocodeEntityField.DESTINATION)) {
                carpool.setDestinationEventualAddress(eventualAddress);
            }

            carpoolRepository.save(carpool);
        } else if (event.getEntity().equals(GeocodeEntity.RIDEREQUEST)) {
            RideRequest request = rideRequestRepository.findById(event.getEntityId()).orElseThrow();
            if (event.getField().equals(GeocodeEntityField.ORIGIN)) {
                request.setOriginEventualAddress(eventualAddress);
            } else if (event.getField().equals(GeocodeEntityField.DESTINATION)) {
                request.setDestinationEventualAddress(eventualAddress);
            }

            rideRequestRepository.save(request);
        }
    }
}

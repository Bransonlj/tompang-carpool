package com.tompang.carpool.carpool_service.query.projector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.tompang.carpool.carpool_service.common.ContainerizedIntegrationTest;
import com.tompang.carpool.carpool_service.common.DomainTopics;
import com.tompang.carpool.carpool_service.common.GeoUtils;
import com.tompang.carpool.carpool_service.config.RabbitConfig;
import com.tompang.carpool.carpool_service.query.entity.Carpool;
import com.tompang.carpool.carpool_service.query.geocode.dto.ReverseGeocodeJobDto;
import com.tompang.carpool.carpool_service.query.geocode.dto.StaticMapJobDto;
import com.tompang.carpool.carpool_service.query.repository.CarpoolQueryRepository;
import com.tompang.carpool.common.Location;
import com.tompang.carpool.common.Route;
import com.tompang.carpool.event.carpool.CarpoolCreatedEvent;
import com.tompang.carpool.geospatial.enums.GeocodeEntity;
import com.tompang.carpool.geospatial.enums.GeocodeEntityField;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CarpoolProjectorIT extends ContainerizedIntegrationTest {

        @Autowired
        private KafkaTemplate<String, Object> kafkaTemplate;

        @Autowired
        private RabbitTemplate rabbitTemplate;

        @Autowired
        private CarpoolQueryRepository carpoolQueryRepository;

        @SuppressWarnings("unchecked")
        @Test
        void handleCarpoolCreated_createsCarpoolRecord() {
                String carpoolId = UUID.randomUUID().toString();
                CarpoolCreatedEvent carpoolCreatedEvent = CarpoolCreatedEvent.newBuilder()
                        .setCarpoolId(carpoolId)
                        .setDriverId("driver-123")
                        .setAvailableSeats(4)
                        .setArrivalTime(Instant.now().truncatedTo(ChronoUnit.MILLIS))
                        .setRoute(Route.newBuilder()
                                .setOrigin(new Location(1d, 2d))
                                .setDestination(new Location(3d, 4d))
                                .build())
                        .build();

                List<ReverseGeocodeJobDto> expectedReGeoJobs = List.of(
                        new ReverseGeocodeJobDto(1d, 2d, GeocodeEntity.CARPOOL, carpoolId, GeocodeEntityField.ORIGIN),
                        new ReverseGeocodeJobDto(3d, 4d, GeocodeEntity.CARPOOL, carpoolId, GeocodeEntityField.DESTINATION)
                );
                List<StaticMapJobDto> expectedStaticMapJobs = List.of(
                        new StaticMapJobDto(1d, 2d, GeocodeEntity.CARPOOL, carpoolId, GeocodeEntityField.ORIGIN),
                        new StaticMapJobDto(3d, 4d, GeocodeEntity.CARPOOL, carpoolId, GeocodeEntityField.DESTINATION)
                );

                kafkaTemplate.send(DomainTopics.Carpool.CARPOOL_CREATED, carpoolId, carpoolCreatedEvent);
                
                await()
                        .pollInterval(Duration.ofSeconds(3))
                        .atMost(10, TimeUnit.SECONDS)
                        .untilAsserted(() -> {
                                Optional<Carpool> optionalCarpool = carpoolQueryRepository.findById(carpoolId);
                                assertThat(optionalCarpool).isPresent();
                                assertThat(optionalCarpool.get().getDriverId()).isEqualTo(carpoolCreatedEvent.getDriverId());
                                assertThat(optionalCarpool.get().getTotalSeats()).isEqualTo(carpoolCreatedEvent.getAvailableSeats());
                                assertThat(optionalCarpool.get().getArrivalTime()).isEqualTo(carpoolCreatedEvent.getArrivalTime());
                                assertThat(optionalCarpool.get().getOrigin())
                                        .isEqualTo(GeoUtils.createPoint(carpoolCreatedEvent.getRoute().getOrigin()));
                                assertThat(optionalCarpool.get().getDestination())
                                        .isEqualTo(GeoUtils.createPoint(carpoolCreatedEvent.getRoute().getDestination()));
                        });

                ReverseGeocodeJobDto reGeoJob1 = (ReverseGeocodeJobDto) rabbitTemplate.receiveAndConvert(RabbitConfig.REVERSE_GEOCODE_QUEUE, 5000);
                ReverseGeocodeJobDto reGeoJob2 = (ReverseGeocodeJobDto) rabbitTemplate.receiveAndConvert(RabbitConfig.REVERSE_GEOCODE_QUEUE, 5000);
                                
                assertThat(reGeoJob1).isNotNull();
                assertThat(reGeoJob2).isNotNull();
                assertThat(List.of(reGeoJob1, reGeoJob2)).containsExactlyInAnyOrderElementsOf(expectedReGeoJobs);

                StaticMapJobDto staticMapJob1 = (StaticMapJobDto) rabbitTemplate.receiveAndConvert(RabbitConfig.STATIC_MAP_QUEUE, 5000);
                StaticMapJobDto staticMapJob2 = (StaticMapJobDto) rabbitTemplate.receiveAndConvert(RabbitConfig.STATIC_MAP_QUEUE, 5000);
                                
                assertThat(staticMapJob1).isNotNull();
                assertThat(staticMapJob2).isNotNull();
                assertThat(List.of(staticMapJob1, staticMapJob2)).containsExactlyInAnyOrderElementsOf(expectedStaticMapJobs);
        }
}

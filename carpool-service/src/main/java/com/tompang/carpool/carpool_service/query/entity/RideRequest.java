package com.tompang.carpool.carpool_service.query.entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.locationtech.jts.geom.Point;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ride_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequest {

    @Id
    private String id;
    private String riderId;
    private int passengers;

    private Instant startTime;
    private Instant endTime;

    @Column(columnDefinition = "GEOGRAPHY(Point,4326)")
    private Point origin;
    @Column(columnDefinition = "GEOGRAPHY(Point,4326)")
    private Point destination;

    @Embedded
    @Builder.Default
    @AttributeOverrides({
        @AttributeOverride(name = "status", column = @Column(name = "origin_address_status")),
        @AttributeOverride(name = "addressString", column = @Column(name = "origin_address_string"))
    })
    private EventualAddress originEventualAddress = EventualAddress.get();

    @Embedded
    @Builder.Default
    @AttributeOverrides({
        @AttributeOverride(name = "status", column = @Column(name = "destination_address_status")),
        @AttributeOverride(name = "addressString", column = @Column(name = "destination_address_string"))
    })
    private EventualAddress destinationEventualAddress = EventualAddress.get();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20)")
    private RideRequestStatus status = RideRequestStatus.PENDING;

    @Builder.Default
    @ManyToMany(mappedBy = "pendingRideRequests")
    private Set<Carpool> matchedCarpools = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "assigned_carpool_id", nullable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Carpool assignedCarpool;

    private String originImageKey;
    private String destinationImageKey;
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Same object
        if (obj == null || getClass() != obj.getClass()) return false; // Different type

        RideRequest request = (RideRequest) obj;

        // Equality is based on ID only
        return id != null && id.equals(request.id);
    }
}

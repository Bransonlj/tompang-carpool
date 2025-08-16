package com.tompang.carpool.carpool_service.query.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.locationtech.jts.geom.Point;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "carpool")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carpool {
    @Id // must specify manually
    private String id;
    private int totalSeats;
    @Builder.Default
    private int seatsAssigned = 0; // we track the seats assigned to prevent having to join with requests during runtime.
    private String driverId;
    private LocalDateTime arrivalTime;

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
    @OneToMany(mappedBy = "assignedCarpool", orphanRemoval = false)
    private Set<RideRequest> confirmedRideRequests = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
        name = "carpool_request_match",
        joinColumns = @JoinColumn(name = "carpool_id"),
        inverseJoinColumns = @JoinColumn(name = "request_id")
    )
    private Set<RideRequest> pendingRideRequests = new HashSet<>();

    public void incrementSeatsAssigned(int seats) {
        this.seatsAssigned += seats;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Same object
        if (obj == null || getClass() != obj.getClass()) return false; // Different type

        Carpool carpool = (Carpool) obj;

        // Equality is based on ID only
        return id != null && id.equals(carpool.id);
    }
}

package com.tompang.carpool.carpool_service.query.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
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

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // TODO change to actual addresses
    private String origin;
    private String destination;

    @Builder.Default
    @ManyToMany(mappedBy = "pendingRideRequests")
    private Set<Carpool> matchedCarpools = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "assigned_carpool_id", nullable = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Carpool assignedCarpool;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Same object
        if (obj == null || getClass() != obj.getClass()) return false; // Different type

        RideRequest request = (RideRequest) obj;

        // Equality is based on ID only
        return id != null && id.equals(request.id);
    }
}

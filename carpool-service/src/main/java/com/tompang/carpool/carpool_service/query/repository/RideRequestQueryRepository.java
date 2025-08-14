package com.tompang.carpool.carpool_service.query.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tompang.carpool.carpool_service.query.entity.RideRequest;


public interface RideRequestQueryRepository extends JpaRepository<RideRequest, String> {
    List<RideRequest> findAllByRiderId(String riderId);
}

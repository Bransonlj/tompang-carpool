package com.tompang.carpool.carpool_service.query.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tompang.carpool.carpool_service.query.entity.RideRequest;


public interface RideRequestRepository extends JpaRepository<RideRequest, String> {

}

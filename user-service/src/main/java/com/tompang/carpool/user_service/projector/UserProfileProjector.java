package com.tompang.carpool.user_service.projector;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tompang.carpool.event.driver.DriverRegistrationApprovedEvent;
import com.tompang.carpool.user_service.common.KafkaTopics;
import com.tompang.carpool.user_service.exception.ResourceNotFoundException;
import com.tompang.carpool.user_service.model.User;
import com.tompang.carpool.user_service.repository.UserRepository;

@Component
public class UserProfileProjector {

  public final UserRepository repository;

  public UserProfileProjector(UserRepository repository) {
    this.repository = repository;
  }

  @KafkaListener(topics = KafkaTopics.Driver.DRIVER_REGISTRATION_APPROVED, groupId = "user-service")
  public void handleDriverRegistered(DriverRegistrationApprovedEvent event) {
    User profile = repository.findById(event.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("Profile of userId not found: " + event.getUserId()));
    profile.setDriverId(event.getDriverRegistrationId());
    this.repository.save(profile);
  }
  
}

package com.tompang.carpool.profile_service.projector;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tompang.carpool.event.driver.DriverRegistrationApprovedEvent;
import com.tompang.carpool.event.user.UserRegisteredEvent;
import com.tompang.carpool.profile_service.common.KafkaTopics;
import com.tompang.carpool.profile_service.exception.ResourceNotFoundException;
import com.tompang.carpool.profile_service.model.UserProfile;
import com.tompang.carpool.profile_service.repository.UserProfileRepository;

@Component
public class UserProfileProjector {

  public final UserProfileRepository repository;

  public UserProfileProjector(UserProfileRepository repository) {
    this.repository = repository;
  }

  @KafkaListener(topics = KafkaTopics.User.USER_REGISTERED, groupId = "profile-service")
  public void handleUserRegistered(UserRegisteredEvent event) {
    UserProfile userProfile = UserProfile.builder()
        .id(event.getUserId())
        .firstName(event.getFirstName())
        .lastName(event.getLastName())
        .build();
    this.repository.save(userProfile);
  }

  @KafkaListener(topics = KafkaTopics.Driver.DRIVER_REGISTRATION_APPROVED, groupId = "profile-service")
  public void handleDriverRegistered(DriverRegistrationApprovedEvent event) {
    UserProfile profile = repository.findById(event.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("Profile of userId not found: " + event.getUserId()));
    profile.setDriverId(event.getDriverRegistrationId());
    this.repository.save(profile);
  }
  
}

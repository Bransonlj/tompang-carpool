package com.tompang.carpool.profile_service.service;

import org.springframework.stereotype.Service;

import com.tompang.carpool.profile_service.exception.ResourceNotFoundException;
import com.tompang.carpool.profile_service.model.UserProfile;
import com.tompang.carpool.profile_service.repository.UserProfileRepository;

@Service
public class UserProfileService {
  public final UserProfileRepository repository;

  public UserProfileService(UserProfileRepository repository) {
    this.repository = repository;
  }

  public UserProfile getUserProfileById(String id) {
    UserProfile userProfile = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("UserProfile not found"));
    return userProfile;
  }

}

package com.tompang.carpool.user_service.service;

import org.springframework.stereotype.Service;

import com.tompang.carpool.user_service.dto.UserProfileDto;
import com.tompang.carpool.user_service.exception.ResourceNotFoundException;
import com.tompang.carpool.user_service.model.User;
import com.tompang.carpool.user_service.repository.UserRepository;

@Service
public class UserProfileService {
  public final UserRepository repository;

  public UserProfileService(UserRepository repository) {
    this.repository = repository;
  }

  public UserProfileDto getUserProfileById(String id) {
    User user = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("UserProfile not found"));
    return UserProfileDto.fromEntity(user);
  }

}

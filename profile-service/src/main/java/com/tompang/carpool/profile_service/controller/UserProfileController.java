package com.tompang.carpool.profile_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.profile_service.dto.UserProfileResopnseDto;
import com.tompang.carpool.profile_service.model.UserProfile;
import com.tompang.carpool.profile_service.service.UserProfileService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

  private final UserProfileService userProfileService;

  public UserProfileController(UserProfileService userProfileService) {
    this.userProfileService = userProfileService;
  }

  @GetMapping("{id}")
  public ResponseEntity<UserProfileResopnseDto> getMethodName(@PathVariable String id) {
    UserProfile userProfile = userProfileService.getUserProfileById(id);
    return ResponseEntity.ok().body(UserProfileResopnseDto.builder()
        .id(userProfile.getId())
        .username(userProfile.getUsername())
        .fullName(userProfile.getFirstName() + " " + userProfile.getLastName())
        .driverId(userProfile.getDriverId())
        .build());
  }
  
}

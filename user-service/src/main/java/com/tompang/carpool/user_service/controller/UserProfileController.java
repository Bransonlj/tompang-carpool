package com.tompang.carpool.user_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.user_service.dto.UserProfileDto;
import com.tompang.carpool.user_service.service.UserProfileService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/user/profile")
public class UserProfileController {

  private final UserProfileService userProfileService;

  public UserProfileController(UserProfileService userProfileService) {
    this.userProfileService = userProfileService;
  }

  @GetMapping("{id}")
  public ResponseEntity<UserProfileDto> getMethodName(@PathVariable String id) {
    UserProfileDto userProfile = userProfileService.getUserProfileById(id);
    return ResponseEntity.ok().body(userProfile);
  }
  
}

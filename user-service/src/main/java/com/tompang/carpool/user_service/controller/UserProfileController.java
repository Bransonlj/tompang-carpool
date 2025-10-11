package com.tompang.carpool.user_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tompang.carpool.user_service.dto.UserProfileDto;
import com.tompang.carpool.user_service.exception.BadRequestException;
import com.tompang.carpool.user_service.service.UserProfileService;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/user/profile")
public class UserProfileController {

  private final UserProfileService userProfileService;

  public UserProfileController(UserProfileService userProfileService) {
    this.userProfileService = userProfileService;
  }

  @GetMapping("{id}")
  public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable String id) {
    UserProfileDto userProfile = userProfileService.getUserProfileById(id);
    return ResponseEntity.ok().body(userProfile);
  }

  @PostMapping("batch")
  public ResponseEntity<Map<String, UserProfileDto>> getUsersByIds(@RequestBody List<String> ids) {
    return ResponseEntity.ok(userProfileService.getUserProfilesMappedByIds(ids));
  }

  @PostMapping(value = "picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> uploadProfilePicture(
    @RequestPart("file") MultipartFile file,
    @RequestPart("userId") String userId
  ) {

    // Check file is not empty
    if (file.isEmpty()) {
        throw new BadRequestException("File is empty");
    }

    // Validate file type
    String contentType = file.getContentType();
    if (contentType == null || 
        !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
        throw new BadRequestException("Only JPEG and PNG files are allowed");
    }

    userProfileService.uploadUserProfilePicture(userId, file);
    URI location = URI.create("/api/user/profile/" + userId);
    return ResponseEntity.created(location).build();
  }
  
}

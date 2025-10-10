package com.tompang.carpool.user_service.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tompang.carpool.user_service.dto.UserProfileDto;
import com.tompang.carpool.user_service.exception.ResourceNotFoundException;
import com.tompang.carpool.user_service.model.User;
import com.tompang.carpool.user_service.repository.UserRepository;

@Service
public class UserProfileService {
  public final UserRepository repository;
  public final S3Service s3Service;

  public UserProfileService(UserRepository repository, S3Service s3Service) {
    this.repository = repository;
    this.s3Service = s3Service;
  }

  public UserProfileDto getUserProfileById(String id) {
    User user = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("UserProfile not found"));
    UserProfileDto dto =  UserProfileDto.fromEntity(user);
    if (user.hasProfilePicture) {
      String profilePictureUrl = s3Service.getFileUrl(user.getId(), S3Service.Directory.PROFILE_PICTURE);
      dto.setProfilePictureUrl(profilePictureUrl);
    }
    return dto;
  }

  public void uploadUserProfilePicture(String userId, MultipartFile file) {
    User user = repository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("UserProfile not found"));
    try {
      s3Service.uploadFile(userId, S3Service.Directory.PROFILE_PICTURE, file);
      user.setHasProfilePicture(true);
      repository.save(user);
    } catch (IOException exception) {
      throw new RuntimeException("Error uploading image");
    }
  }

  public Map<String, UserProfileDto> getUserProfilesMappedByIds(List<String> ids) {
    List<User> users = repository.findAllById(ids);
    Map<String, UserProfileDto> userMap = new HashMap<>();
    UserProfileDto dto;
    for (User user : users) {
      dto = UserProfileDto.fromEntity(user);
      if (user.hasProfilePicture) {
        String profilePictureUrl = s3Service.getFileUrl(user.getId(), S3Service.Directory.PROFILE_PICTURE);
        dto.setProfilePictureUrl(profilePictureUrl);
      }

      userMap.put(user.getId(), dto);
    }

    return userMap;
  }

}

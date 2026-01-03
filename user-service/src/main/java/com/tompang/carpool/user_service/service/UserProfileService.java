package com.tompang.carpool.user_service.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tompang.carpool.user_service.mapper.UserProfileMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tompang.carpool.user_service.dto.UserProfileDto;
import com.tompang.carpool.user_service.exception.ResourceNotFoundException;
import com.tompang.carpool.user_service.model.User;
import com.tompang.carpool.user_service.repository.UserRepository;

@Service
public class UserProfileService {
  private final UserRepository repository;
  private final S3Service s3Service;
  private final UserProfileMapper userProfileMapper;

  public UserProfileService(UserRepository repository, S3Service s3Service, UserProfileMapper userProfileMapper) {
    this.repository = repository;
    this.s3Service = s3Service;
    this.userProfileMapper = userProfileMapper;
  }

  public UserProfileDto getUserProfileById(String id) {
    User user = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("UserProfile not found"));
    UserProfileDto dto;
    if (user.hasProfilePicture) {
      String profilePictureUrl = s3Service.getFileUrl(user.getId(), S3Service.Directory.PROFILE_PICTURE);
      dto =  userProfileMapper.toDto(user, profilePictureUrl);
    } else {
      dto =  userProfileMapper.toDto(user);
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

  /**
   * Fetches and returns a map of UserProfiles from the list of user ids.
   * @param ids
   * @param includePhoto whether to generate and include the photo url.
   * @return
   */
  public Map<String, UserProfileDto> getUserProfilesMappedByIds(List<String> ids, boolean includePhoto) {
    List<User> users = repository.findAllById(ids);
    Map<String, UserProfileDto> userMap = new HashMap<>();
    UserProfileDto dto;
    for (User user : users) {
      if (includePhoto && user.hasProfilePicture) {
        String profilePictureUrl = s3Service.getFileUrl(user.getId(), S3Service.Directory.PROFILE_PICTURE);
        dto =  userProfileMapper.toDto(user, profilePictureUrl);
      } else {
        dto =  userProfileMapper.toDto(user);
      }

      userMap.put(user.getId(), dto);
    }

    return userMap;
  }

}

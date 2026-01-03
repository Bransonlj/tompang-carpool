package com.tompang.carpool.user_service.mapper;

import com.tompang.carpool.user_service.dto.UserProfileDto;
import com.tompang.carpool.user_service.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {

    public UserProfileDto toDto(User entity) {
        return toDto(entity, null);
    }

    public UserProfileDto toDto(User entity, String profilePictureUrl) {
        return new UserProfileDto(
          entity.getId(),
          entity.getFirstName(),
          entity.getLastName(),
          entity.getFirstName() + " " + entity.getLastName()   ,
          entity.getEmail(),
          entity.getDriverId(),
          profilePictureUrl
        );
    }
}

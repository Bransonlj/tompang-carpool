package com.tompang.carpool.user_service.dto;

import com.tompang.carpool.user_service.model.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileDto {
    public String firstName;
    public String lastName;
    public String email;

    // null if user not a driver
    public String driverId;

    public String profilePictureUrl;

    public static UserProfileDto fromEntity(User user) {
        return UserProfileDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .driverId(user.getDriverId())
                .build();
    }
}

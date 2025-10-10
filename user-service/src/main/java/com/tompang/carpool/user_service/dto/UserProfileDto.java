package com.tompang.carpool.user_service.dto;

import com.tompang.carpool.user_service.model.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileDto {
    public String id;
    public String firstName;
    public String lastName;
    public String fullName;
    public String email;

    // null if user not a driver
    public String driverId;

    // null if not profile picture
    public String profilePictureUrl;

    public static UserProfileDto fromEntity(User user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .driverId(user.getDriverId())
                .build();
    }
}

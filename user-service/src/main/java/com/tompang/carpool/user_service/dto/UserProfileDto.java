package com.tompang.carpool.user_service.dto;

import lombok.Builder;

@Builder
public record UserProfileDto(
        String id,
        String firstName,
        String lastName,
        String fullName,
        String email,
        // null if user not a driver
        String driverId,
        // null if not profile picture
        String profilePictureUrl
) {
}

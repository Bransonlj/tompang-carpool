package com.tompang.carpool.user_service.dto;

import com.tompang.carpool.user_service.model.User;

public class UserProfileDto {
    public final String firstName;
    public final String lastName;
    public final String email;

    // null if user not a driver
    public final String driverId;

    public UserProfileDto(
        String firstName,
        String lastName,
        String email,
        String driverId
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.driverId = driverId;
    }

    public static UserProfileDto fromEntity(User user) {
        return new UserProfileDto(
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getDriverId()
        );
    }
}

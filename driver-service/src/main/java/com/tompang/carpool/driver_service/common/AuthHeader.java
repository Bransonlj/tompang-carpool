package com.tompang.carpool.driver_service.common;

import java.util.List;

public class AuthHeader {
    public static final String USER_ID = "X-User-Id";
    public static final String USER_ROLES = "X-User-Roles";

    private AuthHeader() {}

    public static boolean hasAdminRole(String userRoles) {
        return List.of(userRoles.split(",")).contains("ADMIN");
    }
}

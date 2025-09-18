package com.tompang.carpool.user_service.dto;

import java.util.Set;

import com.tompang.carpool.user_service.model.UserRole;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    public String userId;
    public String token;
    public Set<UserRole> roles; 
}

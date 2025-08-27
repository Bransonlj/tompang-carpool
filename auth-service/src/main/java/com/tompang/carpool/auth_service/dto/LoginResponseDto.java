package com.tompang.carpool.auth_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    public String userId;
    public String token;
}

package com.tompang.carpool.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorResponseDto {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}

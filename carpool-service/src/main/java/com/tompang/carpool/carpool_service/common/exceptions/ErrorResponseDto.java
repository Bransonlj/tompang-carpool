package com.tompang.carpool.carpool_service.common.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorResponseDto {
    public final String timestamp;
    public final int status;
    public final String error;
    public final String message;
    public final String path;
}

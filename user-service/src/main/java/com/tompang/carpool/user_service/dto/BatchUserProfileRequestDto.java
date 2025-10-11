package com.tompang.carpool.user_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class BatchUserProfileRequestDto {
    public List<String> ids;
    public boolean includePhoto = false; // default
}

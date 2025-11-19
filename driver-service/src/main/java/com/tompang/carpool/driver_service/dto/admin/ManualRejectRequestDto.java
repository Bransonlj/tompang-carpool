package com.tompang.carpool.driver_service.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ManualRejectRequestDto {
    @NotBlank
    private String rejectReason;
}

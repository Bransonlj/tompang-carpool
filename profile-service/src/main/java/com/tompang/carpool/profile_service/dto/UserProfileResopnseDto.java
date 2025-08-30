package com.tompang.carpool.profile_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResopnseDto {
  public String id;
  public String fullName;
  public String driverId;
}

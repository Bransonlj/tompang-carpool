package com.tompang.carpool.profile_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfile {

  @Id // id must be provided manually
  public String id;
  public String firstName;
  public String lastName;

  // null if user not a driver
  @Builder.Default
  public String driverId = null;
}

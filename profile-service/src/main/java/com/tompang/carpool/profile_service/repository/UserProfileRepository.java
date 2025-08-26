package com.tompang.carpool.profile_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tompang.carpool.profile_service.model.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {

}

package com.tompang.carpool.user_service.model;


import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
@Table(name = "user_profile")  // avoid conflict with reserved table names
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true)
    private String email;

    private String password;

    public String firstName;
    public String lastName;

    // null if user not a driver
    @Builder.Default
    public String driverId = null;

    @ElementCollection(fetch = FetchType.EAGER) 
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "user_roles", // join table
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "roles")
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>(Set.of(UserRole.USER));;
}
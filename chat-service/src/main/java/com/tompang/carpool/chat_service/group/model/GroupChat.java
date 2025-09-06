package com.tompang.carpool.chat_service.group.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupChat {

    @Id
    private String carpoolGroupId;

    @Builder.Default
    @OneToMany(mappedBy = "groupChat", cascade = CascadeType.ALL, orphanRemoval = true) // groupChatUser is dependent on groupChat
    private Set<GroupChatUser> users = new HashSet<>();

}

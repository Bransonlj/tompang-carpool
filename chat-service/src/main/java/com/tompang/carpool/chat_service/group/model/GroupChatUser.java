package com.tompang.carpool.chat_service.group.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
public class GroupChatUser {

    @EmbeddedId
    private GroupChatUserId groupChatUserId;

    @Enumerated(EnumType.STRING)
    private UserTitle title; // use enum

    @ManyToOne
    @MapsId("groupId") // maps groupId in embedded id
    @JoinColumn(name = "group_id")
    private GroupChat groupChat;
}

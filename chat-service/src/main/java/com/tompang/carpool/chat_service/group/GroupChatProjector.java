package com.tompang.carpool.chat_service.group;

import java.util.Collections;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tompang.carpool.chat_service.common.KafkaTopics;
import com.tompang.carpool.chat_service.group.model.GroupChat;
import com.tompang.carpool.chat_service.group.model.GroupChatUser;
import com.tompang.carpool.chat_service.group.model.GroupChatUserId;
import com.tompang.carpool.chat_service.group.model.UserTitle;
import com.tompang.carpool.event.carpool.CarpoolCreatedEvent;
import com.tompang.carpool.event.ride_request.RideRequestAcceptedEvent;

@Component
public class GroupChatProjector {

    private static final String GROUP_ID = "chat-service-projector";
    private final GroupChatRepository repository;

    public GroupChatProjector(GroupChatRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = KafkaTopics.Carpool.CARPOOL_CREATED, groupId = GROUP_ID)
    public void createGroupWithDriver(CarpoolCreatedEvent event) {
        String groupId = event.getCarpoolId(); // group chat id is carpool id

        GroupChat groupChat = GroupChat.builder()
                .carpoolGroupId(groupId)
                .build();
        
        GroupChatUser driverUser = GroupChatUser.builder()
                .groupChatUserId(GroupChatUserId.builder()
                        .groupId(groupId)
                        .userId(event.getDriverId())
                        .build())
                .title(UserTitle.DRIVER)
                .groupChat(groupChat)
                .build();


        groupChat.setUsers(Collections.singleton(driverUser));
        repository.save(groupChat); // no need to save user because cascade=all
    }

    @KafkaListener(topics = KafkaTopics.RideRequest.REQUEST_ACCEPTED, groupId = GROUP_ID)
    public void addRiderToGroup(RideRequestAcceptedEvent event) {
        String groupId = event.getCarpoolId(); // group chat id is carpool id
        GroupChat groupChat = repository.getReferenceById(groupId);
        GroupChatUser riderUser = GroupChatUser.builder()
                .groupChatUserId(GroupChatUserId.builder()
                        .groupId(groupId)
                        .userId(event.getRiderId())
                        .build())
                .title(UserTitle.RIDER)
                .groupChat(groupChat)
                .build();
        groupChat.getUsers().add(riderUser);
        repository.save(groupChat);
    }

}

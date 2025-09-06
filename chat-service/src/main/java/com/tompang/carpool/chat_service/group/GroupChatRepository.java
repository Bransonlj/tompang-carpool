package com.tompang.carpool.chat_service.group;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tompang.carpool.chat_service.group.model.GroupChat;

public interface GroupChatRepository extends JpaRepository<GroupChat, String> {
    
}

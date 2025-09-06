package com.tompang.carpool.chat_service.group;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tompang.carpool.chat_service.group.model.GroupChatUser;
import com.tompang.carpool.chat_service.group.model.GroupChatUserId;

public interface GroupChatUserRepository extends JpaRepository<GroupChatUser, GroupChatUserId> {
    
}

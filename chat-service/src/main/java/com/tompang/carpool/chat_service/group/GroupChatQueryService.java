package com.tompang.carpool.chat_service.group;

import org.springframework.stereotype.Service;

import com.tompang.carpool.chat_service.group.model.GroupChat;
import com.tompang.carpool.chat_service.group.model.GroupChatUser;
import com.tompang.carpool.chat_service.group.model.GroupChatUserId;

@Service
public class GroupChatQueryService {
    private final GroupChatRepository groupChatRepository;
    private final GroupChatUserRepository groupChatUserRepository;

    public GroupChatQueryService(GroupChatRepository groupChatRepository, GroupChatUserRepository groupChatUserRepository) {
        this.groupChatRepository = groupChatRepository;
        this.groupChatUserRepository = groupChatUserRepository;
    }

    public GroupChat getGroupChatById(String groupId) {
        return this.groupChatRepository.getReferenceById(groupId);
    }

    public GroupChatUser getGroupChatUserById(GroupChatUserId id) {
        return this.groupChatUserRepository.getReferenceById(id);
    }
}

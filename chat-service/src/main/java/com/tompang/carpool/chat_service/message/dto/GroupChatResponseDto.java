package com.tompang.carpool.chat_service.message.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupChatResponseDto {
    private String groupId;
    private List<ChatMessageResponseDto> messages;
    private List<GroupMemberDto> members;
}

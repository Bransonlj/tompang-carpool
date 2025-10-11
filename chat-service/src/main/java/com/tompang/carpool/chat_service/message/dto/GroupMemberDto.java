package com.tompang.carpool.chat_service.message.dto;

import com.tompang.carpool.chat_service.group.model.UserTitle;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupMemberDto {
    private String userId;
    private UserTitle userTitle;
}

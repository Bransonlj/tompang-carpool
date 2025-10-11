package com.tompang.carpool.chat_service.message.dto;

import java.time.LocalDateTime;

import com.tompang.carpool.chat_service.message.model.ChatMessage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessageResponseDto {
    private String groupId; // carpoolId
    private LocalDateTime createdAt;
    private String messageId;
    private String senderId;
    private String message;

    public static ChatMessageResponseDto fromEntity(ChatMessage entity) {
        return ChatMessageResponseDto.builder()
            .groupId(entity.getKey().getGroupId())
            .createdAt(entity.getKey().getCreatedAt())
            .messageId(entity.getKey().getMessageId().toString())
            .senderId(entity.getSenderId())
            .message(entity.getMessage())
            .build();
    }
}

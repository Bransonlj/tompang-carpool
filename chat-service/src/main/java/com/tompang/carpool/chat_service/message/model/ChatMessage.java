package com.tompang.carpool.chat_service.message.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Table
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChatMessage {
    @PrimaryKey()
    private ChatMessageKey key; // carpoolId

    private String senderId;
    private String message;
}

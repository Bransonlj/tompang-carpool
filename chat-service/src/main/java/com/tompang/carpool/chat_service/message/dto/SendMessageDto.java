package com.tompang.carpool.chat_service.message.dto;

import lombok.Data;

@Data
public class SendMessageDto {
    public String groupId;
    public String senderId;
    public String message;
}

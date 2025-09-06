package com.tompang.carpool.chat_service.message.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageDto {
    @NotBlank
    public String groupId;
    @NotBlank
    public String senderId;
    @NotBlank
    public String message;
}

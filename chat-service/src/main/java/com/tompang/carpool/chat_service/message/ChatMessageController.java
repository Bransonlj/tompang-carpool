package com.tompang.carpool.chat_service.message;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.chat_service.message.dto.SendMessageDto;
import com.tompang.carpool.chat_service.message.model.ChatMessage;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/chat")
public class ChatMessageController {

    private final ChatMessageOrchestrator chatMessageOrchestrator;

    public ChatMessageController(ChatMessageOrchestrator chatMessageOrchestrator) {
        this.chatMessageOrchestrator = chatMessageOrchestrator;
    }

    @PostMapping("send")
    public ResponseEntity<Void> sendMessage(@RequestBody SendMessageDto dto) {
        ChatMessage createdMessage = this.chatMessageOrchestrator.sendMessage(dto);
        URI location = URI.create("/api/chat/" + createdMessage.getKey().getMessageId());
        return ResponseEntity.created(location).build();
    }
}

package com.tompang.carpool.chat_service.message;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.chat_service.message.dto.SendMessageDto;
import com.tompang.carpool.chat_service.message.model.ChatMessage;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/chat")
public class ChatMessageController {

    private final ChatMessageOrchestrator chatMessageOrchestrator;
    private final ChatMessageService chatMessageService;

    public ChatMessageController(ChatMessageOrchestrator chatMessageOrchestrator, ChatMessageService chatMessageService) {
        this.chatMessageOrchestrator = chatMessageOrchestrator;
        this.chatMessageService = chatMessageService;
    }

    @GetMapping("messages/{gid}")
    public ResponseEntity<List<ChatMessage>> getMessagesByGroupId(@PathVariable String gid) {
        return ResponseEntity.ok(chatMessageService.getMessagesByGroupId(gid));
    }
    

    @PostMapping("send")
    public ResponseEntity<Void> sendMessage(@RequestBody @Valid SendMessageDto dto) {
        ChatMessage createdMessage = this.chatMessageOrchestrator.sendMessage(dto);
        URI location = URI.create("/api/chat/" + createdMessage.getKey().getMessageId());
        return ResponseEntity.created(location).build();
    }
}

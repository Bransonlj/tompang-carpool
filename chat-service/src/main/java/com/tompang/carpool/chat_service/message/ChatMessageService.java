package com.tompang.carpool.chat_service.message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tompang.carpool.chat_service.group.model.UserTitle;
import com.tompang.carpool.chat_service.message.model.ChatMessage;
import com.tompang.carpool.chat_service.message.model.ChatMessageKey;

@Service
public class ChatMessageService {

    private final ChatMessageRepository repository;

    public ChatMessageService(ChatMessageRepository repository) {
        this.repository = repository;
    }

    public ChatMessage createMessage(String groupId, String senderId, UserTitle userTitle, String message) {
        ChatMessage chatMessage = ChatMessage.builder()
                .key(new ChatMessageKey(groupId, LocalDateTime.now(), UUID.randomUUID()))
                .senderId(senderId)
                .senderTitle(userTitle)
                .message(message)
                .build();
        
        ChatMessage savedMessage = this.repository.save(chatMessage);
        return savedMessage;
    }

    public List<ChatMessage> getMessagesByGroupId(String groupId) {
        return this.repository.findByKeyGroupIdOrderByKeyCreatedAtAsc(groupId);
    }
}

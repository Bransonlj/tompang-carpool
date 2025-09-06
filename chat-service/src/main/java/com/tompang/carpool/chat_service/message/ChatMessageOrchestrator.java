package com.tompang.carpool.chat_service.message;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tompang.carpool.chat_service.common.KafkaTopics;
import com.tompang.carpool.chat_service.common.exception.BadRequestException;
import com.tompang.carpool.chat_service.group.GroupChatQueryService;
import com.tompang.carpool.chat_service.group.model.GroupChat;
import com.tompang.carpool.chat_service.group.model.GroupChatUser;
import com.tompang.carpool.chat_service.group.model.GroupChatUserId;
import com.tompang.carpool.chat_service.message.dto.SendMessageDto;
import com.tompang.carpool.chat_service.message.model.ChatMessage;
import com.tompang.carpool.event.chat.ChatMessageSentEvent;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ChatMessageOrchestrator {
    private final GroupChatQueryService groupChatQueryService;
    private final ChatMessageService chatMessageService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ChatMessageOrchestrator(
        GroupChatQueryService groupChatQueryService,
        ChatMessageService chatMessageService,
        KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.groupChatQueryService = groupChatQueryService;
        this.chatMessageService = chatMessageService;
        this.kafkaTemplate = kafkaTemplate;
    }

    public ChatMessage sendMessage(SendMessageDto dto) {
        System.out.println(dto);
        GroupChat groupChat = groupChatQueryService.getGroupChatById(dto.getGroupId());
        GroupChatUser sender;
        try {
            sender = groupChatQueryService.getGroupChatUserById(GroupChatUserId.builder().groupId(dto.groupId).userId(dto.senderId).build());
        } catch (EntityNotFoundException exception) {
            throw new BadRequestException("User is not in group");
        }
        List<String> groupChatUsers = groupChat.getUsers().stream().map(user -> user.getGroupChatUserId().getUserId()).toList();
        ChatMessage createdMessage = chatMessageService.createMessage(sender.getGroupChatUserId().getGroupId(),  sender.getGroupChatUserId().getUserId(), sender.getTitle(), dto.message);
        kafkaTemplate.send(KafkaTopics.Chat.CHAT_MESSAGE_SENT, ChatMessageSentEvent.newBuilder()
                .setMessageId(createdMessage.getKey().getMessageId().toString())
                .setGroupChatId(createdMessage.getKey().getGroupId())
                .setGroupChatUsers(groupChatUsers)
                .setSenderUserId(createdMessage.getSenderId())
                .setCreatedAt(createdMessage.getKey().getCreatedAt())
                .setMessage(createdMessage.getMessage())
                .build());
        return createdMessage;
    }
}

package com.tompang.carpool.chat_service.message;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tompang.carpool.chat_service.group.GroupChatQueryService;
import com.tompang.carpool.chat_service.message.dto.GroupChatResponseDto;
import com.tompang.carpool.chat_service.message.dto.ChatMessageResponseDto;
import com.tompang.carpool.chat_service.message.dto.GroupMemberDto;
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
    private final GroupChatQueryService groupChatQueryService;

    public ChatMessageController(ChatMessageOrchestrator chatMessageOrchestrator, ChatMessageService chatMessageService, GroupChatQueryService groupChatQueryService) {
        this.chatMessageOrchestrator = chatMessageOrchestrator;
        this.chatMessageService = chatMessageService;
        this.groupChatQueryService = groupChatQueryService;
    }

    @GetMapping("group/{gid}")
    public ResponseEntity<GroupChatResponseDto> getGroupChatData(@PathVariable String gid) {
        List<ChatMessageResponseDto> messages = chatMessageService.getMessagesByGroupId(gid).stream()
                .map(chat -> ChatMessageResponseDto.fromEntity(chat)).toList();

        List<GroupMemberDto> groupMembers = groupChatQueryService.getGroupChatById(gid).getUsers().stream().map(user -> GroupMemberDto.builder().userId(user.getGroupChatUserId().getUserId()).userTitle(user.getTitle()).build()).toList();
        return ResponseEntity.ok(GroupChatResponseDto.builder().groupId(gid).messages(messages).members(groupMembers).build());
    }

    @PostMapping("send")
    public ResponseEntity<Void> sendMessage(@RequestBody @Valid SendMessageDto dto) {
        ChatMessage createdMessage = this.chatMessageOrchestrator.sendMessage(dto);
        URI location = URI.create("/api/chat/" + createdMessage.getKey().getMessageId());
        return ResponseEntity.created(location).build();
    }
}

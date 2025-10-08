package com.tompang.carpool.chat_service.message;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.tompang.carpool.chat_service.message.model.ChatMessage;
import com.tompang.carpool.chat_service.message.model.ChatMessageKey;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, ChatMessageKey> {

  List<ChatMessage> findByKeyGroupIdOrderByKeyCreatedAtAsc(String groupId);
}

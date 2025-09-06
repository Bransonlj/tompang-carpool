package com.tompang.carpool.chat_service.message;

import org.springframework.data.repository.CrudRepository;

import com.tompang.carpool.chat_service.message.model.ChatMessage;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, String> {

}

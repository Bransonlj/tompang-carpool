package com.tompang.carpool.chat_service.message.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@PrimaryKeyClass
public class ChatMessageKey implements Serializable {
    @PrimaryKeyColumn(name = "group_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String groupId; // carpoolId

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private LocalDateTime createdAt;

    @PrimaryKeyColumn(name = "message_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private UUID messageId;
}

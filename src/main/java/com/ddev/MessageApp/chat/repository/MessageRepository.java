package com.ddev.MessageApp.chat.repository;

import com.ddev.MessageApp.chat.model.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Messages, UUID> {
    Page<Messages> findByConversationsIdOrderBySentAtDesc(Integer id, Pageable pageable);
    Page<Messages> findByConversationsIdAndSentAtLessThan(
            Integer conversationId,
            LocalDateTime sentAt,
            Pageable pageable
    );

    @Query(value = """
            SELECT COUNT(*) FROM Messages m
            WHERE m.sentAt > :date AND m.conversations.id = :conversationId
            """)
    Integer countUnseenMessages(@Param("date") LocalDateTime date, @Param("conversationId") Integer conversationId);
}

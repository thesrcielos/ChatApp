package com.ddev.MessageApp.chat.repository;

import com.ddev.MessageApp.chat.model.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Messages, UUID> {
    Page<Messages> findByConversationsIdOrderBySentAtAsc(Integer id, Pageable pageable);
}

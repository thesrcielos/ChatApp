package com.ddev.MessageApp.chat.repository;

import com.ddev.MessageApp.chat.model.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<ChatEntity, Integer> {

}

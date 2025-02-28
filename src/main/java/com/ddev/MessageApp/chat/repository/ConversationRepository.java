package com.ddev.MessageApp.chat.repository;

import com.ddev.MessageApp.chat.model.Conversations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversations, Integer> {

}

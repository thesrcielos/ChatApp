package com.ddev.MessageApp.chat.repository;


import com.ddev.MessageApp.chat.model.GroupConversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupConversationRepository extends JpaRepository<GroupConversation, Integer> {

}
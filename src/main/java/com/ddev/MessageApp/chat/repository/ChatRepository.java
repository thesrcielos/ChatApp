package com.ddev.MessageApp.chat.repository;

import com.ddev.MessageApp.chat.model.ChatEntity;
import com.ddev.MessageApp.user.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ChatRepository extends JpaRepository<ChatEntity, Integer> {
    Page<ChatEntity> findByUserId(Integer userId, Pageable pageable);

    @Query(value = "SELECT user_id FROM chats WHERE conversation_id = :conversationId AND user_id <> :userId LIMIT 1", nativeQuery = true)
    Optional<Integer> findUserIdByConversationAndNotUser(@Param("conversationId") Integer conversationId, @Param("userId") Integer userId);
    @Query("""
        SELECT c.conversation.id FROM ChatEntity c 
        WHERE c.user.id IN (:userId1, :userId2)
        AND c.conversation.type = ConversationType.CHAT
        GROUP BY c.conversation.id
        HAVING COUNT(DISTINCT c.user.id) = 2
    """)
    Optional<Integer> findConversationIdByUsers(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);

    @Query(value = "SELECT user_id FROM chats WHERE conversation_id = :conversationId", nativeQuery = true)
    List<Integer> getUserListFromChat(@Param("conversationId") Integer conversationId);

    @Query(
            value = """
        SELECT u.email
        FROM users u
        JOIN chats c ON c.user_id = u.id
        WHERE c.conversation_id = :conversationId
        AND u.email != :userEmail
        """,
            nativeQuery = true
    )
    List<String> getUserEmailListFromChat(@Param("conversationId") Integer conversationId, @Param("userEmail") String userEmail);

    @Query(value = """
            SELECT c.user
            FROM ChatEntity c
            WHERE c.conversation.id = :cId
            """)
    List<UserEntity> getUsersChatInfo(@Param("cId") Integer cId);
}

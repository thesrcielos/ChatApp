package com.ddev.MessageApp.chat.model;

import com.ddev.MessageApp.user.model.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Chats")
public class ChatEntity {
    @EmbeddedId
    private ChatPK id;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("conversationId")
    private Conversations conversation;
    @ManyToOne
    @MapsId("userId")
    private UserEntity user;
}

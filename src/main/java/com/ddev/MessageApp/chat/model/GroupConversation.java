package com.ddev.MessageApp.chat.model;

import com.ddev.MessageApp.user.model.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "GroupConversations")
@Getter
@Setter
public class GroupConversation extends Conversations {
    private String name;
    private UserEntity createdBy;
    private String description;
}

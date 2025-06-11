package com.ddev.MessageApp.chat.model;

import com.ddev.MessageApp.user.model.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Conversations")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;
    protected LocalDate createdAt;
    protected ConversationType type;
    protected LocalDateTime lastActivity;
}

package com.ddev.MessageApp.chat.model;

import com.ddev.MessageApp.user.model.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Messages {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    @ManyToOne
    private Conversations conversations;
    @ManyToOne
    private UserEntity user;
    private String message;
    private LocalDateTime sentAt;
    private boolean seen;
    private FileType type;
    private String url;
    private String fileType;
}

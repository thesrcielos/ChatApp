package com.ddev.MessageApp.chat.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ChatPK implements Serializable {
    private Integer conversationId;
    private Integer userId;
}

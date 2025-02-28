package com.ddev.MessageApp.user.dto;

import com.ddev.MessageApp.user.model.Status;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {
    @NotNull(message = "The user id mustn't be null")
    private Integer userId;
    @NotNull(message = "The contact id mustn't be null")
    private Integer contactId;
}

package com.ddev.MessageApp.user.dto;

import com.ddev.MessageApp.user.model.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {
    @NotNull(message = "The user id mustn't be null")
    @Positive(message = "Id must be greater than 0")
    private Integer userId;
    @Positive(message = "Id must be greater than 0")
    @NotNull(message = "The contact id mustn't be null")
    private Integer contactId;
}

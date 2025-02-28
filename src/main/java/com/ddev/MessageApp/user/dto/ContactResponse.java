package com.ddev.MessageApp.user.dto;

import com.ddev.MessageApp.user.model.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponse {
    private Integer id;
    private Integer contact;
    private String contactName;
    private LocalDateTime createdAt;
}

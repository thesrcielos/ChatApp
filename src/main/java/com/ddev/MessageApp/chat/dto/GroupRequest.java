package com.ddev.MessageApp.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GroupRequest {
    @NotBlank(message = "Group name cannot be blank")
    @Size(max = 100, message = "Group name must not exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Group description must not exceed 500 characters")
    private String description;

    @NotNull(message = "User ID cannot be null")
    @Positive(message = "User ID must be a positive number")
    private Integer userId;

    @NotNull(message = "Group users list cannot be null")
    @Size(min = 1, message = "At least one user must be included in the group")
    private List<@NotNull(message = "User ID in group cannot be null") @Positive(message = "User ID must be a positive number") Integer> groupUsers;
}

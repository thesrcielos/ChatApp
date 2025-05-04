package com.ddev.MessageApp.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDTO {
    private Integer id;
    private String name;
    private String email;
}

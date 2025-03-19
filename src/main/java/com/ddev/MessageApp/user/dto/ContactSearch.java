package com.ddev.MessageApp.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContactSearch {
    private Integer userId;
    private String email;
    private String name;
}

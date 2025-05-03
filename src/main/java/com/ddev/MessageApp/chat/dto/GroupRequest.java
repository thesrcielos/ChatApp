package com.ddev.MessageApp.chat.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GroupRequest {
    private String name;
    private Integer userId;
    private List<Integer> groupUsers;
}

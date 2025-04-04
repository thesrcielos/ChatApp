package com.ddev.MessageApp.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupRequest {
    private String name;
    private Integer userId;
    private List<Integer> groupUsers;
}

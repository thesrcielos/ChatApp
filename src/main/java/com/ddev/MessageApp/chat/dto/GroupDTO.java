package com.ddev.MessageApp.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupDTO {
    private String name;
    private List<Integer> groupUsers;
}

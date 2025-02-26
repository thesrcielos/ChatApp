package com.ddev.MessageApp.chat.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PaginatedListObject<T> {
    private List<T> values;
    private int currentPage;
    private int totalPages;
    private long totalItems;
}

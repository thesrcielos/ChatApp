package com.ddev.MessageApp.chat.util;

public class PaginationValidator {

    private static final int MAX_PAGE_SIZE = 100;
    private static final int MIN_PAGE_SIZE = 1;
    private static final int MIN_PAGE_NUMBER = 0;

    public static void validatePagination(int page, int size) {
        if (page < MIN_PAGE_NUMBER) {
            throw new IllegalArgumentException("Page index must not be negative");
        }
        if (size < MIN_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must be at least " + MIN_PAGE_SIZE);
        }
        if (size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must not exceed " + MAX_PAGE_SIZE);
        }
    }
}

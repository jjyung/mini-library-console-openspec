package com.example.library.controller.dto;

import com.example.library.domain.BookStatusEnum;

public record BookSummaryResponseDTO(
    String bookId,
    String title,
    String isbn,
    String author,
    String category,
    BookStatusEnum status,
    int availableCount,
    int totalCount,
    String borrowedByReaderId
) {}

package com.example.library.controller.dto;

import com.example.library.domain.BookStatusEnum;
import java.util.List;

public record BookSummaryResponseDTO(
    String bookId,
    String title,
    String author,
    int totalCopies,
    int availableCopies,
    int checkedOutCopies,
    BookStatusEnum status,
    List<ActiveTransactionResponseDTO> activeTransactions
) {
}

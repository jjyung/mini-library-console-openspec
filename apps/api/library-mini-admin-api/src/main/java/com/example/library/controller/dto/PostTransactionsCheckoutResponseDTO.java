package com.example.library.controller.dto;

public record PostTransactionsCheckoutResponseDTO(
    BookSummaryResponseDTO book,
    ActiveTransactionResponseDTO transaction
) {
}

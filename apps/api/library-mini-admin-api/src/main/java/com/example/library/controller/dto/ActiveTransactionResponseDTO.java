package com.example.library.controller.dto;

import java.time.OffsetDateTime;

public record ActiveTransactionResponseDTO(
    String transactionId,
    String borrowerName,
    OffsetDateTime checkedOutAt
) {
}

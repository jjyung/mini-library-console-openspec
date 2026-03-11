package com.example.library.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record PostTransactionsCheckoutRequestDTO(
    @NotBlank(message = "must not be blank") String bookId,
    @NotBlank(message = "must not be blank") String borrowerName
) {
}

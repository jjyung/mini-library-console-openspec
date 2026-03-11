package com.example.library.controller.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;

public record PostTransactionsCheckoutRequestDTO(
    @NotBlank(message = "must not be blank") String isbn,
    @NotBlank(message = "must not be blank") String readerId,
    LocalDate dueDate
) {
}

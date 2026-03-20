package com.example.library.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record PostLoansReturnRequestDTO(
    @NotBlank(message = "must not be blank") String isbn,
    String readerId
) {}

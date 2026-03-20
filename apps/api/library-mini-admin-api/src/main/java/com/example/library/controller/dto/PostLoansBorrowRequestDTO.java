package com.example.library.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record PostLoansBorrowRequestDTO(
    @NotBlank(message = "must not be blank") String isbn,
    @NotBlank(message = "must not be blank") String readerId,
    String dueDate
) {}

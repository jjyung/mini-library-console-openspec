package com.example.library.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PostBooksRequestDTO(
    @NotBlank(message = "must not be blank") String title,
    @NotBlank(message = "must not be blank") String isbn,
    String author,
    @NotBlank(message = "must not be blank") String category,
    @Min(value = 1L, message = "must be greater than or equal to 1") int quantity,
    @NotNull(message = "must not be null") Boolean active
) {}

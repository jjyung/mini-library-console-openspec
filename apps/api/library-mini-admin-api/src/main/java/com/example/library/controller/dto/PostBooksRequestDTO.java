package com.example.library.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PostBooksRequestDTO(
    @NotBlank(message = "must not be blank") String title,
    @NotBlank(message = "must not be blank") String isbn,
    @NotBlank(message = "must not be blank") String author,
    @NotBlank(message = "must not be blank") String category,
    boolean active,
    @Min(value = 1L, message = "must be greater than or equal to 1") int initialCopies
) {
}

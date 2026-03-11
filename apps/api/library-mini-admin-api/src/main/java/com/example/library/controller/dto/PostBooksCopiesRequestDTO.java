package com.example.library.controller.dto;

import jakarta.validation.constraints.Min;

public record PostBooksCopiesRequestDTO(
    @Min(value = 1L, message = "must be greater than or equal to 1") int additionalCopies
) {
}

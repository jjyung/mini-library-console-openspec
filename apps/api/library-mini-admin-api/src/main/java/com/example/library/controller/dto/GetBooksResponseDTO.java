package com.example.library.controller.dto;

import java.util.List;

public record GetBooksResponseDTO(List<BookSummaryResponseDTO> items) {}

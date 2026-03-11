package com.example.library.controller;

import com.example.library.api.ApiResponse;
import com.example.library.controller.dto.PostTransactionsCheckoutRequestDTO;
import com.example.library.controller.dto.PostTransactionsCheckoutResponseDTO;
import com.example.library.controller.dto.PostTransactionsReturnResponseDTO;
import com.example.library.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final LibraryService libraryService;

    public TransactionController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping("/checkout")
    public ApiResponse<PostTransactionsCheckoutResponseDTO> checkout(
        @Valid @RequestBody PostTransactionsCheckoutRequestDTO request
    ) {
        return ApiResponse.success(
            "Checkout completed successfully",
            this.libraryService.checkoutBook(request.bookId(), request.borrowerName())
        );
    }

    @PostMapping("/{transactionId}/return")
    public ApiResponse<PostTransactionsReturnResponseDTO> returnBook(@PathVariable String transactionId) {
        return ApiResponse.success(
            "Return completed successfully",
            this.libraryService.returnBook(transactionId)
        );
    }
}

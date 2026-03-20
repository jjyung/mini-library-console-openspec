package com.example.library.controller;

import com.example.library.api.ApiResponse;
import com.example.library.controller.dto.PostLoansBorrowRequestDTO;
import com.example.library.controller.dto.PostLoansBorrowResponseDTO;
import com.example.library.controller.dto.PostLoansReturnRequestDTO;
import com.example.library.controller.dto.PostLoansReturnResponseDTO;
import com.example.library.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class TransactionController {

    private final LibraryService libraryService;

    public TransactionController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping("/borrow")
    public ApiResponse<PostLoansBorrowResponseDTO> borrow(
        @Valid @RequestBody PostLoansBorrowRequestDTO request
    ) {
        return ApiResponse.success(
            "Book borrowed successfully",
            this.libraryService.borrowBook(request.isbn(), request.readerId())
        );
    }

    @PostMapping("/return")
    public ApiResponse<PostLoansReturnResponseDTO> returnBook(
        @Valid @RequestBody PostLoansReturnRequestDTO request
    ) {
        return ApiResponse.success(
            "Return completed successfully",
            this.libraryService.returnBook(request.isbn(), request.readerId())
        );
    }
}

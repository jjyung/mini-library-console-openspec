package com.example.library.controller;

import com.example.library.api.ApiResponse;
import com.example.library.controller.dto.GetBooksResponseDTO;
import com.example.library.controller.dto.PostBooksCopiesRequestDTO;
import com.example.library.controller.dto.PostBooksRequestDTO;
import com.example.library.controller.dto.PostBooksResponseDTO;
import com.example.library.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final LibraryService libraryService;

    public BookController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping
    public ApiResponse<GetBooksResponseDTO> listBooks() {
        return ApiResponse.success("Books fetched successfully", this.libraryService.listBooks());
    }

    @PostMapping
    public ApiResponse<PostBooksResponseDTO> createBook(@Valid @RequestBody PostBooksRequestDTO request) {
        return ApiResponse.success(
            "Book created successfully",
            this.libraryService.createBook(request.title(), request.author(), request.initialCopies())
        );
    }

    @PostMapping("/{bookId}/copies")
    public ApiResponse<PostBooksResponseDTO> addCopies(
        @PathVariable String bookId,
        @Valid @RequestBody PostBooksCopiesRequestDTO request
    ) {
        return ApiResponse.success(
            "Copies added successfully",
            this.libraryService.addCopies(bookId, request.additionalCopies())
        );
    }
}

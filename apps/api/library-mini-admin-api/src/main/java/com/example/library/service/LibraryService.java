package com.example.library.service;

import com.example.library.controller.dto.GetBooksResponseDTO;
import com.example.library.controller.dto.PostBooksResponseDTO;
import com.example.library.controller.dto.PostLoansBorrowResponseDTO;
import com.example.library.controller.dto.PostLoansReturnResponseDTO;

public interface LibraryService {

    GetBooksResponseDTO listBooks(String keyword, String status);

    PostBooksResponseDTO createBook(
        String title,
        String isbn,
        String author,
        String category,
        int quantity,
        boolean active
    );

    PostLoansBorrowResponseDTO borrowBook(String isbn, String readerId);

    PostLoansReturnResponseDTO returnBook(String isbn, String readerId);
}

package com.example.library.service;

import java.time.LocalDate;
import com.example.library.controller.dto.GetBooksResponseDTO;
import com.example.library.controller.dto.PostBooksResponseDTO;
import com.example.library.controller.dto.PostTransactionsCheckoutResponseDTO;
import com.example.library.controller.dto.PostTransactionsReturnResponseDTO;

public interface LibraryService {

    GetBooksResponseDTO listBooks();

    PostBooksResponseDTO createBook(
        String title,
        String isbn,
        String author,
        String category,
        boolean active,
        int initialCopies
    );

    PostBooksResponseDTO addCopies(String bookId, int additionalCopies);

    PostTransactionsCheckoutResponseDTO checkoutBook(String isbn, String readerId, LocalDate dueDate);

    PostTransactionsReturnResponseDTO returnBook(String isbn, String readerId);
}

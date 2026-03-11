package com.example.library.service;

import com.example.library.controller.dto.GetBooksResponseDTO;
import com.example.library.controller.dto.PostBooksResponseDTO;
import com.example.library.controller.dto.PostTransactionsCheckoutResponseDTO;
import com.example.library.controller.dto.PostTransactionsReturnResponseDTO;

public interface LibraryService {

    GetBooksResponseDTO listBooks();

    PostBooksResponseDTO createBook(String title, String author, int initialCopies);

    PostBooksResponseDTO addCopies(String bookId, int additionalCopies);

    PostTransactionsCheckoutResponseDTO checkoutBook(String bookId, String borrowerName);

    PostTransactionsReturnResponseDTO returnBook(String transactionId);
}

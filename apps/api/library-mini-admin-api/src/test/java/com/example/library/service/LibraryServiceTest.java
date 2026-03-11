package com.example.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.library.controller.dto.GetBooksResponseDTO;
import com.example.library.controller.dto.PostBooksResponseDTO;
import com.example.library.controller.dto.PostTransactionsCheckoutResponseDTO;
import com.example.library.controller.dto.PostTransactionsReturnResponseDTO;
import com.example.library.exception.ClientErrorException;
import com.example.library.repository.InMemoryBookRepository;
import com.example.library.repository.InMemoryBorrowTransactionRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LibraryServiceTest {

    private LibraryService libraryService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-03-11T09:30:00Z"), ZoneOffset.UTC);
        this.libraryService = new LibraryServiceImpl(
            new InMemoryBookRepository(),
            new InMemoryBorrowTransactionRepository(),
            fixedClock
        );
    }

    @Test
    void shouldCreateListCheckoutAndReturnBook() {
        PostBooksResponseDTO createdBook = this.libraryService.createBook("Clean Code", "Robert C. Martin", 2);
        String bookId = createdBook.book().bookId();

        PostBooksResponseDTO updatedBook = this.libraryService.addCopies(bookId, 1);
        assertThat(updatedBook.book().totalCopies()).isEqualTo(3);
        assertThat(updatedBook.book().availableCopies()).isEqualTo(3);

        PostTransactionsCheckoutResponseDTO checkoutResponse = this.libraryService.checkoutBook(bookId, "Samson");
        assertThat(checkoutResponse.book().availableCopies()).isEqualTo(2);
        assertThat(checkoutResponse.book().checkedOutCopies()).isEqualTo(1);
        assertThat(checkoutResponse.transaction().borrowerName()).isEqualTo("Samson");

        PostTransactionsReturnResponseDTO returnResponse = this.libraryService.returnBook(
            checkoutResponse.transaction().transactionId()
        );
        assertThat(returnResponse.book().availableCopies()).isEqualTo(3);
        assertThat(returnResponse.book().checkedOutCopies()).isEqualTo(0);

        GetBooksResponseDTO booksResponse = this.libraryService.listBooks();
        assertThat(booksResponse.books()).hasSize(1);
        assertThat(booksResponse.books().getFirst().activeTransactions()).isEmpty();
    }

    @Test
    void shouldRejectCheckoutWhenNoCopiesAreAvailable() {
        PostBooksResponseDTO createdBook = this.libraryService.createBook("DDD", "Eric Evans", 1);
        this.libraryService.checkoutBook(createdBook.book().bookId(), "Samson");

        assertThatThrownBy(() -> this.libraryService.checkoutBook(createdBook.book().bookId(), "Taylor"))
            .isInstanceOf(ClientErrorException.class)
            .hasMessage("No available copies for checkout");
    }

    @Test
    void shouldRejectReturningCompletedTransaction() {
        PostBooksResponseDTO createdBook = this.libraryService.createBook("DDIA", "Martin Kleppmann", 1);
        PostTransactionsCheckoutResponseDTO checkoutResponse = this.libraryService.checkoutBook(
            createdBook.book().bookId(),
            "Samson"
        );
        this.libraryService.returnBook(checkoutResponse.transaction().transactionId());

        assertThatThrownBy(() -> this.libraryService.returnBook(checkoutResponse.transaction().transactionId()))
            .isInstanceOf(ClientErrorException.class)
            .hasMessage("Transaction is not returnable");
    }
}

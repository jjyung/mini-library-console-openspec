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
        PostBooksResponseDTO createdBook = this.libraryService.createBook(
            "Clean Code",
            "9780132350884",
            "Robert C. Martin",
            "technology",
            true,
            2
        );
        String bookId = createdBook.book().bookId();

        PostBooksResponseDTO updatedBook = this.libraryService.addCopies(bookId, 1);
        assertThat(updatedBook.book().totalCopies()).isEqualTo(3);
        assertThat(updatedBook.book().availableCopies()).isEqualTo(3);

        PostTransactionsCheckoutResponseDTO checkoutResponse = this.libraryService.checkoutBook(
            "9780132350884",
            "Samson",
            null
        );
        assertThat(checkoutResponse.book().availableCopies()).isEqualTo(2);
        assertThat(checkoutResponse.book().checkedOutCopies()).isEqualTo(1);
        assertThat(checkoutResponse.transaction().borrowerName()).isEqualTo("Samson");

        PostTransactionsReturnResponseDTO returnResponse = this.libraryService.returnBook("9780132350884", "Samson");
        assertThat(returnResponse.book().availableCopies()).isEqualTo(3);
        assertThat(returnResponse.book().checkedOutCopies()).isEqualTo(0);
        assertThat(createdBook.book().isbn()).isEqualTo("9780132350884");
        assertThat(createdBook.book().category()).isEqualTo("technology");

        GetBooksResponseDTO booksResponse = this.libraryService.listBooks();
        assertThat(booksResponse.books()).hasSize(1);
        assertThat(booksResponse.books().getFirst().activeTransactions()).isEmpty();
    }

    @Test
    void shouldRejectCheckoutWhenNoCopiesAreAvailable() {
        PostBooksResponseDTO createdBook = this.libraryService.createBook(
            "DDD",
            "9780321125217",
            "Eric Evans",
            "technology",
            true,
            1
        );
        this.libraryService.checkoutBook(createdBook.book().isbn(), "Samson", null);

        assertThatThrownBy(() -> this.libraryService.checkoutBook(createdBook.book().isbn(), "Taylor", null))
            .isInstanceOf(ClientErrorException.class)
            .hasMessage("No available copies for checkout");
    }

    @Test
    void shouldRejectReturningWhenActiveTransactionDoesNotExist() {
        PostBooksResponseDTO createdBook = this.libraryService.createBook(
            "DDIA",
            "9781449373320",
            "Martin Kleppmann",
            "technology",
            true,
            1
        );
        PostTransactionsCheckoutResponseDTO checkoutResponse = this.libraryService.checkoutBook(
            createdBook.book().isbn(),
            "Samson",
            null
        );
        this.libraryService.returnBook(createdBook.book().isbn(), "Samson");

        assertThatThrownBy(() -> this.libraryService.returnBook(checkoutResponse.book().isbn(), "Samson"))
            .isInstanceOf(ClientErrorException.class)
            .hasMessage("Active transaction not found");
    }

    @Test
    void shouldRejectCheckoutForInactiveBook() {
        this.libraryService.createBook("Refactoring", "9780201485677", "Martin Fowler", "technology", false, 1);

        assertThatThrownBy(() -> this.libraryService.checkoutBook("9780201485677", "Samson", null))
            .isInstanceOf(ClientErrorException.class)
            .hasMessage("Book is inactive");
    }
}

package com.example.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void shouldCreateBorrowReturnAndSearchBookByIsbn() {
        this.libraryService.createBook(
            "Clean Code",
            "978-0-13-235088-4",
            "Robert C. Martin",
            "technology",
            2,
            true
        );

        assertThat(this.libraryService.listBooks(null, null).items()).hasSize(1);
        assertThat(this.libraryService.listBooks("235088", null).items()).hasSize(1);

        var borrowResponse = this.libraryService.borrowBook("978-0-13-235088-4", "samson");
        assertThat(borrowResponse.book().availableCount()).isEqualTo(1);
        assertThat(borrowResponse.book().status().name()).isEqualTo("AVAILABLE");

        var secondBorrowResponse = this.libraryService.borrowBook("978-0-13-235088-4", "taylor");
        assertThat(secondBorrowResponse.book().availableCount()).isZero();
        assertThat(secondBorrowResponse.book().status().name()).isEqualTo("BORROWED");

        var returnResponse = this.libraryService.returnBook("978-0-13-235088-4", "samson");
        assertThat(returnResponse.book().availableCount()).isEqualTo(1);
        assertThat(returnResponse.book().status().name()).isEqualTo("AVAILABLE");
    }

    @Test
    void shouldRejectDuplicateIsbnAndInvalidBorrowState() {
        this.libraryService.createBook(
            "Domain-Driven Design",
            "978-0-321-12521-7",
            "Eric Evans",
            "technology",
            1,
            true
        );

        assertThatThrownBy(() ->
            this.libraryService.createBook(
                    "Duplicate DDD",
                    "978-0-321-12521-7",
                    "Another Author",
                    "technology",
                    1,
                    true
                )
        )
            .isInstanceOf(ClientErrorException.class)
            .hasMessage("ISBN 已存在，無法重複新增");

        this.libraryService.borrowBook("978-0-321-12521-7", "samson");

        assertThatThrownBy(() -> this.libraryService.borrowBook("978-0-321-12521-7", "taylor"))
            .isInstanceOf(ClientErrorException.class)
            .hasMessage("該書籍已全數借出");
    }

    @Test
    void shouldRejectBorrowForInactiveBookAndReturnWithoutLoan() {
        this.libraryService.createBook(
            "Designing Data-Intensive Applications",
            "978-1-449-37332-0",
            "Martin Kleppmann",
            "technology",
            1,
            false
        );

        assertThatThrownBy(() -> this.libraryService.borrowBook("978-1-449-37332-0", "samson"))
            .isInstanceOf(ClientErrorException.class)
            .hasMessage("該書籍未上架，無法借閱");

        assertThatThrownBy(() -> this.libraryService.returnBook("978-1-449-37332-0", null))
            .isInstanceOf(ClientErrorException.class)
            .hasMessage("該書籍沒有借出記錄");
    }
}

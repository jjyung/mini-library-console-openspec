package com.example.library.domain;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class BorrowTransaction {

    private final String transactionId;
    private final String bookId;
    private final String readerId;
    private final OffsetDateTime checkedOutAt;
    private final LocalDate dueDate;
    private OffsetDateTime returnedAt;

    public BorrowTransaction(
        String transactionId,
        String bookId,
        String readerId,
        OffsetDateTime checkedOutAt,
        LocalDate dueDate,
        OffsetDateTime returnedAt
    ) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.readerId = readerId;
        this.checkedOutAt = checkedOutAt;
        this.dueDate = dueDate;
        this.returnedAt = returnedAt;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public String getBookId() {
        return this.bookId;
    }

    public String getReaderId() {
        return this.readerId;
    }

    public OffsetDateTime getCheckedOutAt() {
        return this.checkedOutAt;
    }

    public LocalDate getDueDate() {
        return this.dueDate;
    }

    public OffsetDateTime getReturnedAt() {
        return this.returnedAt;
    }

    public boolean isActive() {
        return this.returnedAt == null;
    }

    public void markReturned(OffsetDateTime returnedDateTime) {
        this.returnedAt = returnedDateTime;
    }
}

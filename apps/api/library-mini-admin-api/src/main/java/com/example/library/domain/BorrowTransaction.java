package com.example.library.domain;

import java.time.OffsetDateTime;

public class BorrowTransaction {

    private final String transactionId;
    private final String bookId;
    private final String borrowerName;
    private final OffsetDateTime checkedOutAt;
    private OffsetDateTime returnedAt;

    public BorrowTransaction(
        String transactionId,
        String bookId,
        String borrowerName,
        OffsetDateTime checkedOutAt,
        OffsetDateTime returnedAt
    ) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.borrowerName = borrowerName;
        this.checkedOutAt = checkedOutAt;
        this.returnedAt = returnedAt;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public String getBookId() {
        return this.bookId;
    }

    public String getBorrowerName() {
        return this.borrowerName;
    }

    public OffsetDateTime getCheckedOutAt() {
        return this.checkedOutAt;
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

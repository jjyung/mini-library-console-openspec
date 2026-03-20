package com.example.library.domain;

public class Book {

    private final String bookId;
    private final String title;
    private final String isbn;
    private final String author;
    private final String category;
    private final boolean active;
    private int totalCount;
    private int availableCount;
    private String borrowedByReaderId;
    private BookStatusEnum status;

    public Book(
        String bookId,
        String title,
        String isbn,
        String author,
        String category,
        int totalCount,
        boolean active
    ) {
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
        this.author = author;
        this.category = category;
        this.active = active;
        this.totalCount = totalCount;
        this.availableCount = totalCount;
        this.status = active ? BookStatusEnum.AVAILABLE : BookStatusEnum.INACTIVE;
    }

    public String getBookId() {
        return this.bookId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getIsbn() {
        return this.isbn;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getCategory() {
        return this.category;
    }

    public boolean getActive() {
        return this.active;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public int getAvailableCount() {
        return this.availableCount;
    }

    public String getBorrowedByReaderId() {
        return this.borrowedByReaderId;
    }

    public BookStatusEnum getStatus() {
        return this.status;
    }

    public void borrow(String readerId) {
        this.availableCount -= 1;
        this.borrowedByReaderId = readerId;
        this.status = this.availableCount == 0 ? BookStatusEnum.BORROWED : BookStatusEnum.AVAILABLE;
    }

    public void returnBook() {
        this.availableCount += 1;
        this.borrowedByReaderId = null;
        this.status = this.active ? BookStatusEnum.AVAILABLE : BookStatusEnum.INACTIVE;
    }
}

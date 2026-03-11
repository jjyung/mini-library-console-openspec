package com.example.library.domain;

public class Book {

    private final String bookId;
    private final String title;
    private final String isbn;
    private final String author;
    private final String category;
    private boolean active;
    private int totalCopies;

    public Book(String bookId, String title, String isbn, String author, String category, boolean active, int totalCopies) {
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
        this.author = author;
        this.category = category;
        this.active = active;
        this.totalCopies = totalCopies;
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

    public boolean isActive() {
        return this.active;
    }

    public int getTotalCopies() {
        return this.totalCopies;
    }

    public void addCopies(int additionalCopies) {
        this.totalCopies += additionalCopies;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

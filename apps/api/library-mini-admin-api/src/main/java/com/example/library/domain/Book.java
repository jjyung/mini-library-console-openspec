package com.example.library.domain;

public class Book {

    private final String bookId;
    private final String title;
    private final String author;
    private int totalCopies;

    public Book(String bookId, String title, String author, int totalCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
    }

    public String getBookId() {
        return this.bookId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

    public int getTotalCopies() {
        return this.totalCopies;
    }

    public void addCopies(int additionalCopies) {
        this.totalCopies += additionalCopies;
    }
}

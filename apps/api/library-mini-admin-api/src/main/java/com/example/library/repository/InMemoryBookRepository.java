package com.example.library.repository;

import com.example.library.domain.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryBookRepository implements BookRepository {

    private final ConcurrentMap<String, Book> bookStore = new ConcurrentHashMap<>();

    @Override
    public Book save(Book book) {
        this.bookStore.put(book.getBookId(), book);
        return book;
    }

    @Override
    public Optional<Book> findById(String bookId) {
        return Optional.ofNullable(this.bookStore.get(bookId));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(this.bookStore.values());
    }
}

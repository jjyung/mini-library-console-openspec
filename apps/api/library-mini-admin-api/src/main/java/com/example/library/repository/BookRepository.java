package com.example.library.repository;

import com.example.library.domain.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {

    Book save(Book book);

    Optional<Book> findById(String bookId);

    List<Book> findAll();
}

package com.example.library.repository;

import com.example.library.domain.BorrowTransaction;
import java.util.List;
import java.util.Optional;

public interface BorrowTransactionRepository {

    BorrowTransaction save(BorrowTransaction borrowTransaction);

    Optional<BorrowTransaction> findById(String transactionId);

    List<BorrowTransaction> findAll();

    List<BorrowTransaction> findByBookId(String bookId);
}

package com.example.library.repository;

import com.example.library.domain.BorrowTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryBorrowTransactionRepository implements BorrowTransactionRepository {

    private final ConcurrentMap<String, BorrowTransaction> transactionStore = new ConcurrentHashMap<>();

    @Override
    public BorrowTransaction save(BorrowTransaction borrowTransaction) {
        this.transactionStore.put(borrowTransaction.getTransactionId(), borrowTransaction);
        return borrowTransaction;
    }

    @Override
    public Optional<BorrowTransaction> findById(String transactionId) {
        return Optional.ofNullable(this.transactionStore.get(transactionId));
    }

    @Override
    public List<BorrowTransaction> findAll() {
        return new ArrayList<>(this.transactionStore.values());
    }

    @Override
    public List<BorrowTransaction> findByBookId(String bookId) {
        return this.transactionStore.values().stream()
            .filter(borrowTransaction -> borrowTransaction.getBookId().equals(bookId))
            .toList();
    }
}

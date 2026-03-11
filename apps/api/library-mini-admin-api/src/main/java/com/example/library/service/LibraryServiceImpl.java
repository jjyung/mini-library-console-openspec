package com.example.library.service;

import com.example.library.controller.dto.ActiveTransactionResponseDTO;
import com.example.library.controller.dto.BookSummaryResponseDTO;
import com.example.library.controller.dto.GetBooksResponseDTO;
import com.example.library.controller.dto.PostBooksResponseDTO;
import com.example.library.controller.dto.PostTransactionsCheckoutResponseDTO;
import com.example.library.controller.dto.PostTransactionsReturnResponseDTO;
import com.example.library.domain.Book;
import com.example.library.domain.BookStatusEnum;
import com.example.library.domain.BorrowTransaction;
import com.example.library.exception.ClientErrorException;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowTransactionRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class LibraryServiceImpl implements LibraryService {

    private final BookRepository bookRepository;
    private final BorrowTransactionRepository borrowTransactionRepository;
    private final Clock clock;

    public LibraryServiceImpl(
        BookRepository bookRepository,
        BorrowTransactionRepository borrowTransactionRepository,
        Clock clock
    ) {
        this.bookRepository = bookRepository;
        this.borrowTransactionRepository = borrowTransactionRepository;
        this.clock = clock;
    }

    @Override
    public GetBooksResponseDTO listBooks() {
        List<BookSummaryResponseDTO> books = this.bookRepository.findAll().stream()
            .sorted(Comparator.comparing(Book::getTitle))
            .map(this::toBookSummaryResponse)
            .toList();
        return new GetBooksResponseDTO(books);
    }

    @Override
    public PostBooksResponseDTO createBook(String title, String author, int initialCopies) {
        Book book = new Book(
            UUID.randomUUID().toString(),
            normalizeRequiredValue(title, "title"),
            normalizeRequiredValue(author, "author"),
            validatePositiveNumber(initialCopies, "initialCopies")
        );
        this.bookRepository.save(book);
        return new PostBooksResponseDTO(toBookSummaryResponse(book));
    }

    @Override
    public PostBooksResponseDTO addCopies(String bookId, int additionalCopies) {
        Book book = getBookOrThrow(bookId);
        book.addCopies(validatePositiveNumber(additionalCopies, "additionalCopies"));
        this.bookRepository.save(book);
        return new PostBooksResponseDTO(toBookSummaryResponse(book));
    }

    @Override
    public PostTransactionsCheckoutResponseDTO checkoutBook(String bookId, String borrowerName) {
        Book book = getBookOrThrow(bookId);
        if (calculateAvailableCopies(book) <= 0) {
            throw new ClientErrorException("No available copies for checkout");
        }

        BorrowTransaction borrowTransaction = new BorrowTransaction(
            UUID.randomUUID().toString(),
            book.getBookId(),
            normalizeRequiredValue(borrowerName, "borrowerName"),
            OffsetDateTime.now(this.clock),
            null
        );
        this.borrowTransactionRepository.save(borrowTransaction);

        return new PostTransactionsCheckoutResponseDTO(
            toBookSummaryResponse(book),
            toActiveTransactionResponse(borrowTransaction)
        );
    }

    @Override
    public PostTransactionsReturnResponseDTO returnBook(String transactionId) {
        BorrowTransaction borrowTransaction = this.borrowTransactionRepository.findById(transactionId)
            .orElseThrow(() -> new ClientErrorException("Transaction not found"));

        if (!borrowTransaction.isActive()) {
            throw new ClientErrorException("Transaction is not returnable");
        }

        borrowTransaction.markReturned(OffsetDateTime.now(this.clock));
        this.borrowTransactionRepository.save(borrowTransaction);

        Book book = getBookOrThrow(borrowTransaction.getBookId());
        return new PostTransactionsReturnResponseDTO(toBookSummaryResponse(book));
    }

    private Book getBookOrThrow(String bookId) {
        return this.bookRepository.findById(normalizeRequiredValue(bookId, "bookId"))
            .orElseThrow(() -> new ClientErrorException("Book not found"));
    }

    private String normalizeRequiredValue(String value, String fieldName) {
        if (value == null) {
            throw new ClientErrorException(fieldName + " must not be blank");
        }

        String normalizedValue = value.trim();
        if (normalizedValue.isEmpty()) {
            throw new ClientErrorException(fieldName + " must not be blank");
        }
        return normalizedValue;
    }

    private int validatePositiveNumber(int value, String fieldName) {
        if (value < 1) {
            throw new ClientErrorException(fieldName + " must be greater than or equal to 1");
        }
        return value;
    }

    private BookSummaryResponseDTO toBookSummaryResponse(Book book) {
        List<ActiveTransactionResponseDTO> activeTransactions = this.borrowTransactionRepository.findByBookId(book.getBookId())
            .stream()
            .filter(BorrowTransaction::isActive)
            .sorted(Comparator.comparing(BorrowTransaction::getCheckedOutAt))
            .map(this::toActiveTransactionResponse)
            .toList();

        int checkedOutCopies = activeTransactions.size();
        int availableCopies = book.getTotalCopies() - checkedOutCopies;
        BookStatusEnum status = availableCopies > 0 ? BookStatusEnum.AVAILABLE : BookStatusEnum.CHECKED_OUT;

        return new BookSummaryResponseDTO(
            book.getBookId(),
            book.getTitle(),
            book.getAuthor(),
            book.getTotalCopies(),
            availableCopies,
            checkedOutCopies,
            status,
            activeTransactions
        );
    }

    private ActiveTransactionResponseDTO toActiveTransactionResponse(BorrowTransaction borrowTransaction) {
        return new ActiveTransactionResponseDTO(
            borrowTransaction.getTransactionId(),
            borrowTransaction.getBorrowerName(),
            borrowTransaction.getCheckedOutAt()
        );
    }

    private int calculateAvailableCopies(Book book) {
        return book.getTotalCopies() - (int) this.borrowTransactionRepository.findByBookId(book.getBookId())
            .stream()
            .filter(BorrowTransaction::isActive)
            .count();
    }
}

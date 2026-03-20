package com.example.library.service;

import com.example.library.api.BusinessCode;
import com.example.library.controller.dto.BookSummaryResponseDTO;
import com.example.library.controller.dto.GetBooksResponseDTO;
import com.example.library.controller.dto.PostBooksResponseDTO;
import com.example.library.controller.dto.PostLoansBorrowResponseDTO;
import com.example.library.controller.dto.PostLoansReturnResponseDTO;
import com.example.library.domain.Book;
import com.example.library.domain.BookStatusEnum;
import com.example.library.exception.ClientErrorException;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowTransactionRepository;
import java.time.Clock;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class LibraryServiceImpl implements LibraryService {

    private final BookRepository bookRepository;
    @SuppressWarnings("unused")
    private final BorrowTransactionRepository borrowTransactionRepository;
    @SuppressWarnings("unused")
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
    public GetBooksResponseDTO listBooks(String keyword, String status) {
        List<BookSummaryResponseDTO> books = this.bookRepository.findAll().stream()
            .filter(book -> matchesKeyword(book, keyword))
            .filter(book -> matchesStatus(book, status))
            .sorted(Comparator.comparing(Book::getTitle))
            .map(this::toBookSummaryResponse)
            .toList();
        return new GetBooksResponseDTO(books);
    }

    @Override
    public PostBooksResponseDTO createBook(
        String title,
        String isbn,
        String author,
        String category,
        int quantity,
        boolean active
    ) {
        String normalizedIsbn = normalizeRequiredValue(isbn, "isbn");
        if (this.bookRepository.findByIsbn(normalizedIsbn).isPresent()) {
            throw new ClientErrorException(BusinessCode.BOOK_ALREADY_EXISTS, "ISBN 已存在，無法重複新增");
        }

        Book book = new Book(
            UUID.randomUUID().toString(),
            normalizeRequiredValue(title, "title"),
            normalizedIsbn,
            normalizeOptionalValue(author),
            normalizeRequiredValue(category, "category"),
            validatePositiveNumber(quantity, "quantity"),
            active
        );
        this.bookRepository.save(book);
        return new PostBooksResponseDTO(toBookSummaryResponse(book));
    }

    @Override
    public PostLoansBorrowResponseDTO borrowBook(String isbn, String readerId) {
        Book book = getBookByIsbnOrThrow(isbn);
        if (book.getStatus() == BookStatusEnum.INACTIVE) {
            throw new ClientErrorException(BusinessCode.BOOK_NOT_BORROWABLE, "該書籍未上架，無法借閱");
        }
        if (book.getAvailableCount() == 0) {
            throw new ClientErrorException(BusinessCode.BOOK_NOT_BORROWABLE, "該書籍已全數借出");
        }

        book.borrow(normalizeRequiredValue(readerId, "readerId"));
        this.bookRepository.save(book);
        return new PostLoansBorrowResponseDTO(toBookSummaryResponse(book));
    }

    @Override
    public PostLoansReturnResponseDTO returnBook(String isbn, String readerId) {
        Book book = getBookByIsbnOrThrow(isbn);
        if (book.getAvailableCount() == book.getTotalCount()) {
            throw new ClientErrorException(BusinessCode.BOOK_NOT_RETURNABLE, "該書籍沒有借出記錄");
        }

        normalizeOptionalValue(readerId);
        book.returnBook();
        this.bookRepository.save(book);
        return new PostLoansReturnResponseDTO(toBookSummaryResponse(book));
    }

    private Book getBookByIsbnOrThrow(String isbn) {
        String normalizedIsbn = normalizeRequiredValue(isbn, "isbn");
        return this.bookRepository.findByIsbn(normalizedIsbn)
            .orElseThrow(() -> new ClientErrorException(BusinessCode.BOOK_NOT_FOUND, "找不到該 ISBN 的書籍"));
    }

    private String normalizeRequiredValue(String value, String fieldName) {
        if (value == null) {
            throw new ClientErrorException(BusinessCode.VALIDATION_ERROR, fieldName + " must not be blank");
        }

        String normalizedValue = value.trim();
        if (normalizedValue.isEmpty()) {
            throw new ClientErrorException(BusinessCode.VALIDATION_ERROR, fieldName + " must not be blank");
        }
        return normalizedValue;
    }

    private String normalizeOptionalValue(String value) {
        if (value == null) {
            return null;
        }
        String normalizedValue = value.trim();
        return normalizedValue.isEmpty() ? null : normalizedValue;
    }

    private int validatePositiveNumber(int value, String fieldName) {
        if (value < 1) {
            throw new ClientErrorException(
                BusinessCode.VALIDATION_ERROR,
                fieldName + " must be greater than or equal to 1"
            );
        }
        return value;
    }

    private BookSummaryResponseDTO toBookSummaryResponse(Book book) {
        return new BookSummaryResponseDTO(
            book.getBookId(),
            book.getTitle(),
            book.getIsbn(),
            book.getAuthor(),
            book.getCategory(),
            book.getStatus(),
            book.getAvailableCount(),
            book.getTotalCount(),
            book.getBorrowedByReaderId()
        );
    }

    private boolean matchesKeyword(Book book, String keyword) {
        String normalizedKeyword = normalizeOptionalValue(keyword);
        if (normalizedKeyword == null) {
            return true;
        }

        String lowerCaseKeyword = normalizedKeyword.toLowerCase(Locale.ROOT);
        return book.getTitle().toLowerCase(Locale.ROOT).contains(lowerCaseKeyword)
            || book.getIsbn().toLowerCase(Locale.ROOT).contains(lowerCaseKeyword)
            || (book.getAuthor() != null && book.getAuthor().toLowerCase(Locale.ROOT).contains(lowerCaseKeyword));
    }

    private boolean matchesStatus(Book book, String status) {
        String normalizedStatus = normalizeOptionalValue(status);
        if (normalizedStatus == null) {
            return true;
        }
        return book.getStatus().name().equalsIgnoreCase(normalizedStatus);
    }
}

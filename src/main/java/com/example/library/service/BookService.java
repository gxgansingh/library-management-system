package com.example.library.service;

import com.example.library.exception.BusinessRuleException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import com.example.library.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public BookService(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    public List<Book> findAll() { return bookRepository.findAll(); }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
    }

    public Book create(Book book) {
        if (book.getTotalCopies() < 1) throw new BusinessRuleException("A book must have at least one copy.");
        if (bookRepository.existsByIsbn(book.getIsbn())) throw new BusinessRuleException("ISBN already exists.");
        book.setAvailableCopies(book.getTotalCopies());
        return bookRepository.save(book);
    }

    @Transactional
    public Book update(Long id, Book input) {
        Book book = findById(id);
        if (input.getTitle() == null || input.getTitle().isBlank()
                || input.getAuthor() == null || input.getAuthor().isBlank()
                || input.getIsbn() == null || input.getIsbn().isBlank()) {
            throw new BusinessRuleException("Title, author and ISBN are required.");
        }
        bookRepository.findByIsbn(input.getIsbn()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) throw new BusinessRuleException("ISBN already exists.");
        });
        int borrowed = book.getTotalCopies() - book.getAvailableCopies();
        if (input.getTotalCopies() < 1 || input.getTotalCopies() < borrowed) {
            throw new BusinessRuleException("Total copies cannot be less than the number currently borrowed.");
        }
        book.setTitle(input.getTitle().trim());
        book.setAuthor(input.getAuthor().trim());
        book.setIsbn(input.getIsbn().trim());
        book.setTotalCopies(input.getTotalCopies());
        book.setAvailableCopies(input.getTotalCopies() - borrowed);
        return bookRepository.save(book);
    }

    @Transactional
    public void delete(Long id) {
        Book book = findById(id);
        if (loanRepository.existsByBookIdAndStatus(id, "ACTIVE")) {
            throw new BusinessRuleException("Return all active loans for this book before deleting it.");
        }
        if (loanRepository.existsByBookId(id)) {
            throw new BusinessRuleException("This book has transaction history and cannot be deleted.");
        }
        bookRepository.delete(book);
    }
}

package com.example.library.service;

import com.example.library.exception.BusinessRuleException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.model.Book;
import com.example.library.model.Loan;
import com.example.library.model.Member;
import com.example.library.repository.BookRepository;
import com.example.library.repository.LoanRepository;
import com.example.library.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {
    private static final int MAX_ACTIVE_LOANS = 3;
    private static final int LOAN_PERIOD_DAYS = 14;

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public LoanService(LoanRepository loanRepository,
                      BookRepository bookRepository,
                      MemberRepository memberRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    public Loan findById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found: " + id));
    }

    public List<Loan> findActiveLoans() {
        return loanRepository.findByStatus("ACTIVE");
    }

    @Transactional
    public Loan borrowBook(Long bookId, Long memberId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + memberId));

        // Business rule 1: a book can only be borrowed when a copy is available.
        if (book.getAvailableCopies() < 1) {
            throw new BusinessRuleException("No available copies of this book.");
        }

        // Business rule 2: each member may have at most three active loans.
        if (loanRepository.findByMemberIdAndStatus(memberId, "ACTIVE").size() >= MAX_ACTIVE_LOANS) {
            throw new BusinessRuleException("A member cannot have more than 3 active loans.");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        LocalDate today = LocalDate.now();
        Loan loan = new Loan(book, member, today, today.plusDays(LOAN_PERIOD_DAYS));
        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found: " + loanId));

        if (!"ACTIVE".equals(loan.getStatus())) {
            throw new BusinessRuleException("This loan has already been returned.");
        }

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        loan.setStatus("RETURNED");
        loan.setReturnDate(LocalDate.now());
        return loanRepository.save(loan);
    }
}

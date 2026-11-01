package com.example.library.service;

import com.example.library.exception.BusinessRuleException;
import com.example.library.model.Book;
import com.example.library.model.Member;
import com.example.library.repository.BookRepository;
import com.example.library.repository.LoanRepository;
import com.example.library.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {
    @Mock LoanRepository loanRepository;
    @Mock BookRepository bookRepository;
    @Mock MemberRepository memberRepository;

    @InjectMocks LoanService loanService;

    private Book book;
    private Member member;

    @BeforeEach
    void setUp() {
        book = new Book("Clean Code", "Robert C. Martin", "ISBN-001", 2);
        book.setId(1L);
        member = new Member("Asha Rao", "asha@example.com", "9876543210");
        member.setId(1L);
    }

    @Test
    void borrowingAvailableBookCreatesLoanAndReducesCopies() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(loanRepository.findByMemberIdAndStatus(1L, "ACTIVE")).thenReturn(List.of());
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loanRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var loan = loanService.borrowBook(1L, 1L);

        assertEquals("ACTIVE", loan.getStatus());
        assertEquals(1, book.getAvailableCopies());
        assertEquals(14, java.time.temporal.ChronoUnit.DAYS.between(
                loan.getLoanDate(), loan.getDueDate()));
        verify(loanRepository).save(any());
    }

    @Test
    void borrowingUnavailableBookIsRejected() {
        book.setAvailableCopies(0);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        assertThrows(BusinessRuleException.class, () -> loanService.borrowBook(1L, 1L));
        verify(loanRepository, never()).save(any());
    }

    @Test
    void memberWithThreeActiveLoansCannotBorrowAnotherBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(loanRepository.findByMemberIdAndStatus(1L, "ACTIVE"))
                .thenReturn(List.of(mock(com.example.library.model.Loan.class),
                        mock(com.example.library.model.Loan.class),
                        mock(com.example.library.model.Loan.class)));

        assertThrows(BusinessRuleException.class, () -> loanService.borrowBook(1L, 1L));
        verify(loanRepository, never()).save(any());
    }
}

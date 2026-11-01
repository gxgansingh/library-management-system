package com.example.library.repository;

import com.example.library.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByMemberIdAndStatus(Long memberId, String status);
    List<Loan> findByStatus(String status);
    List<Loan> findByBookId(Long bookId);
    List<Loan> findByMemberId(Long memberId);
    boolean existsByBookId(Long bookId);
    boolean existsByMemberId(Long memberId);
    boolean existsByBookIdAndStatus(Long bookId, String status);
    boolean existsByMemberIdAndStatus(Long memberId, String status);
}

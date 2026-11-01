package com.example.library.controller;

import com.example.library.model.Loan;
import com.example.library.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanService loanService;
    public LoanController(LoanService loanService) { this.loanService = loanService; }

    @GetMapping public List<Loan> getLoans() { return loanService.findAll(); }
    @GetMapping("/{id}") public Loan getLoan(@PathVariable Long id) { return loanService.findById(id); }
    @GetMapping("/active") public List<Loan> getActiveLoans() { return loanService.findActiveLoans(); }

    @PostMapping("/borrow") @ResponseStatus(HttpStatus.CREATED)
    public Loan borrowBook(@RequestBody Map<String, Long> request) {
        Long bookId = request.get("bookId"), memberId = request.get("memberId");
        if (bookId == null || memberId == null)
            throw new com.example.library.exception.BusinessRuleException("Request must include bookId and memberId.");
        return loanService.borrowBook(bookId, memberId);
    }
    @PostMapping("/{id}/return") public Loan returnBook(@PathVariable Long id) {
        return loanService.returnBook(id);
    }
}

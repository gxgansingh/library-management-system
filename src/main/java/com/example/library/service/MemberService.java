package com.example.library.service;

import com.example.library.exception.BusinessRuleException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.model.Member;
import com.example.library.repository.LoanRepository;
import com.example.library.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public MemberService(MemberRepository memberRepository, LoanRepository loanRepository) {
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
    }

    public List<Member> findAll() { return memberRepository.findAll(); }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + id));
    }

    public Member create(Member member) {
        member.setEmail(member.getEmail().trim().toLowerCase());
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new BusinessRuleException("A member with this email already exists.");
        }
        return memberRepository.save(member);
    }

    @Transactional
    public Member update(Long id, Member input) {
        Member member = findById(id);
        if (input.getName() == null || input.getName().isBlank()
                || input.getEmail() == null || input.getEmail().isBlank()
                || input.getPhone() == null || input.getPhone().isBlank()) {
            throw new BusinessRuleException("Name, email and phone are required.");
        }
        String email = input.getEmail().trim().toLowerCase();
        memberRepository.findByEmail(email).ifPresent(existing -> {
            if (!existing.getId().equals(id)) throw new BusinessRuleException("A member with this email already exists.");
        });
        member.setName(input.getName().trim());
        member.setEmail(email);
        member.setPhone(input.getPhone().trim());
        return memberRepository.save(member);
    }

    @Transactional
    public void delete(Long id) {
        Member member = findById(id);
        if (loanRepository.existsByMemberIdAndStatus(id, "ACTIVE")) {
            throw new BusinessRuleException("Return all active loans before deleting this member.");
        }
        if (loanRepository.existsByMemberId(id)) {
            throw new BusinessRuleException("This member has transaction history and cannot be deleted.");
        }
        memberRepository.delete(member);
    }
}

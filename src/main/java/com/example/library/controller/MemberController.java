package com.example.library.controller;

import com.example.library.model.Member;
import com.example.library.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;
    public MemberController(MemberService memberService) { this.memberService = memberService; }

    @GetMapping public List<Member> getMembers() { return memberService.findAll(); }
    @GetMapping("/{id}") public Member getMember(@PathVariable Long id) { return memberService.findById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Member createMember(@Valid @RequestBody Member member) { return memberService.create(member); }
    @PutMapping("/{id}") public Member updateMember(@PathVariable Long id, @Valid @RequestBody Member member) {
        return memberService.update(id, member);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(@PathVariable Long id) { memberService.delete(id); }
}

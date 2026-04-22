package com.collectivity.controller;

import com.collectivity.dto.CreateMemberDto;
import com.collectivity.dto.CreateMemberPaymentDto;
import com.collectivity.dto.MemberDto;
import com.collectivity.dto.MemberPaymentDto;
import com.collectivity.service.MemberPaymentService;
import com.collectivity.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    private final MemberPaymentService memberPaymentService;

    public MemberController(MemberService memberService, MemberPaymentService memberPaymentService) {
        this.memberService = memberService;
        this.memberPaymentService = memberPaymentService;
    }

    @PostMapping
    public ResponseEntity<List<MemberDto>> createMembers(@RequestBody List<CreateMemberDto> requests) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMembers(requests));
    }

    // New: POST /members/{id}/payments
    @PostMapping("/{id}/payments")
    public ResponseEntity<List<MemberPaymentDto>> createMemberPayments(
            @PathVariable String id,
            @RequestBody List<CreateMemberPaymentDto> requests) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(memberPaymentService.createMemberPayments(id, requests));
    }
}
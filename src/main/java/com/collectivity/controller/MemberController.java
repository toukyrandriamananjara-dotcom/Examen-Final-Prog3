package com.collectivity.controller;

import com.collectivity.dto.CreateMemberDto;
import com.collectivity.dto.MemberDto;
import com.collectivity.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    public MemberController(MemberService memberService) { this.memberService = memberService; }

    @PostMapping
    public ResponseEntity<List<MemberDto>> createMembers(@RequestBody List<CreateMemberDto> requests) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMembers(requests));
    }
}
package com.collectivity.controller;

import com.collectivity.dto.*;
import com.collectivity.service.CollectivityService;
import com.collectivity.service.FinancialAccountService;
import com.collectivity.service.MembershipFeeService;
import com.collectivity.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class CollectivityController {

    private final CollectivityService collectivityService;
    private final MembershipFeeService membershipFeeService;
    private final TransactionService transactionService;
    private final FinancialAccountService financialAccountService;


    public CollectivityController(CollectivityService collectivityService,
                                  MembershipFeeService membershipFeeService,
                                  TransactionService transactionService,
                                  FinancialAccountService financialAccountService) {
        this.collectivityService = collectivityService;
        this.membershipFeeService = membershipFeeService;
        this.transactionService = transactionService;
        this.financialAccountService = financialAccountService;
    }

    @PostMapping
    public ResponseEntity<List<CollectivityDto>> createCollectivities(
            @RequestBody List<CreateCollectivityDto> requests) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(collectivityService.createCollectivities(requests));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CollectivityDto> updateCollectivity(
            @PathVariable String id,
            @RequestBody UpdateCollectivityDto request) {
        return ResponseEntity.ok(collectivityService.updateCollectivity(id, request));
    }

    // GET /collectivities/{id}/membershipFees
    @GetMapping("/{id}/membershipFees")
    public ResponseEntity<List<MembershipFeeDto>> getMembershipFees(@PathVariable String id) {
        return ResponseEntity.ok(membershipFeeService.getMembershipFeesByCollectivity(id));
    }

    //  POST /collectivities/{id}/membershipFees
    @PostMapping("/{id}/membershipFees")
    public ResponseEntity<List<MembershipFeeDto>> createMembershipFees(
            @PathVariable String id,
            @RequestBody List<CreateMembershipFeeDto> requests) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(membershipFeeService.createMembershipFees(id, requests));
    }

    //  GET /collectivities/{id}/transactions
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<CollectivityTransactionDto>> getTransactions(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        return ResponseEntity.ok(transactionService.getTransactionsByCollectivityAndDateRange(id, from, to));
    }

    // GET /collectivities/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CollectivityDto> getCollectivityById(@PathVariable String id) {
        return ResponseEntity.ok(collectivityService.getCollectivityById(id));
    }

    // NEW: GET /collectivities/{id}/financialAccounts
    @GetMapping("/{id}/financialAccounts")
    public ResponseEntity<List<FinancialAccountWithBalanceDto>> getFinancialAccounts(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate at) {
        return ResponseEntity.ok(financialAccountService.getFinancialAccountsWithBalance(id, at));
    }


}
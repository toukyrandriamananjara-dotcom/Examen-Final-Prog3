package com.collectivity.controller;

import com.collectivity.dto.CollectivityLocalStatisticsDto;
import com.collectivity.dto.CollectivityOverallStatisticsDto;
import com.collectivity.service.StatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/collectivites")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<List<CollectivityLocalStatisticsDto>> getLocalStatistics(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        return ResponseEntity.ok(statisticsService.getLocalStatistics(id, from, to));
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<CollectivityOverallStatisticsDto>> getOverallStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        return ResponseEntity.ok(statisticsService.getOverallStatistics(from, to));
    }
}
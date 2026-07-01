package com.example.layout.service;

import java.time.LocalDate;
import java.util.Map;

/**
 * Tách biệt trách nhiệm tính toán lương/thù lao khỏi IChuyenDuLichService (SRP).
 */
public interface ISalaryStatService {
    Map<String, Object> getYearlyStats(int year, Integer staffId);
    Map<String, Object> getMonthlyStats(int year, int month, Integer staffId);
    Map<String, Object> getPeriodStats(LocalDate from, LocalDate to, Integer staffId);
}

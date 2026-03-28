package com.example.layout.service;

import com.example.layout.repository.DatChoRepository;
import com.example.layout.repository.KhachHangRepository;
import com.example.layout.repository.TourRepository;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.ThanhToanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.math.BigDecimal;

@Service
public class NhanVienDashboardService implements INhanVienDashboardService {

    private final DatChoRepository datChoRepository;
    private final KhachHangRepository khachHangRepository;
    private final TourRepository tourRepository;
    private final ChuyenDuLichRepository chuyenDuLichRepository;
    private final ThanhToanRepository thanhToanRepository;

    public NhanVienDashboardService(DatChoRepository datChoRepository,
                                    KhachHangRepository khachHangRepository,
                                    TourRepository tourRepository,
                                    ChuyenDuLichRepository chuyenDuLichRepository,
                                    ThanhToanRepository thanhToanRepository) {
        this.datChoRepository = datChoRepository;
        this.khachHangRepository = khachHangRepository;
        this.tourRepository = tourRepository;
        this.chuyenDuLichRepository = chuyenDuLichRepository;
        this.thanhToanRepository = thanhToanRepository;
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            Long pendingBookings = datChoRepository.countByTrangThai("Chờ xác nhận");
            LocalDate now = LocalDate.now();
            LocalDate nextMonth = now.plusDays(30);
            Long upcomingTrips = chuyenDuLichRepository.countByNgayBatDauBetweenAndTrangThai(now, nextMonth, "Sắp diễn ra");
            Long totalCustomers = khachHangRepository.count();
            Long activeTours = tourRepository.count();

            stats.put("pendingBookings", pendingBookings != null ? pendingBookings : 0);
            stats.put("upcomingTrips", upcomingTrips != null ? upcomingTrips : 0);
            stats.put("totalCustomers", totalCustomers != null ? totalCustomers : 0);
            stats.put("activeTours", activeTours != null ? activeTours : 0);
        } catch (Exception e) {
            stats.put("pendingBookings", 0);
            stats.put("upcomingTrips", 0);
            stats.put("totalCustomers", 0);
            stats.put("activeTours", 0);
        }
        return stats;
    }

    @Override
    public Map<String, Object> getWeekRevenue() {
        Map<String, Object> result = new HashMap<>();
        try {
            LocalDate today = LocalDate.now();
            List<String> labels = Arrays.asList("CN", "T2", "T3", "T4", "T5", "T6", "T7");
            List<BigDecimal> values = new ArrayList<>();
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                BigDecimal revenue = thanhToanRepository.getRevenueByDate(date);
                values.add(revenue != null ? revenue : BigDecimal.ZERO);
            }
            result.put("labels", labels);
            result.put("values", values);
        } catch (Exception e) {
            result.put("labels", Arrays.asList("CN", "T2", "T3", "T4", "T5", "T6", "T7"));
            result.put("values", Arrays.asList(0, 0, 0, 0, 0, 0, 0));
        }
        return result;
    }
}
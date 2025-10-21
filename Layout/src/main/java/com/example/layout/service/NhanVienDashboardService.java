package com.example.layout.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.layout.repository.DatChoRepository;
import com.example.layout.repository.KhachHangRepository;
import com.example.layout.repository.TourRepository;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.ThanhToanRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.math.BigDecimal;
import java.util.Date;

@Service
public class NhanVienDashboardService {

    @Autowired
    private DatChoRepository datChoRepository;
    
    @Autowired
    private KhachHangRepository khachHangRepository;
    
    @Autowired
    private TourRepository tourRepository;
    
    @Autowired
    private ChuyenDuLichRepository chuyenDuLichRepository;
    
    @Autowired
    private ThanhToanRepository thanhToanRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Đếm booking chờ xử lý
            Long pendingBookings = datChoRepository.countByTrangThai("Chờ xác nhận");
            
            // Đếm chuyến đi sắp diễn ra (trong vòng 30 ngày tới)
            LocalDate now = LocalDate.now();
            LocalDate nextMonth = now.plusDays(30);
            Long upcomingTrips = chuyenDuLichRepository.countByNgayBatDauBetweenAndTrangThai(
                now, nextMonth, "Sắp diễn ra");
            
            // Tổng số khách hàng
            Long totalCustomers = khachHangRepository.count();
            
            // Số tour đang hoạt động
            Long activeTours = tourRepository.count();
            
            stats.put("pendingBookings", pendingBookings != null ? pendingBookings : 0);
            stats.put("upcomingTrips", upcomingTrips != null ? upcomingTrips : 0);
            stats.put("totalCustomers", totalCustomers != null ? totalCustomers : 0);
            stats.put("activeTours", activeTours != null ? activeTours : 0);
            
        } catch (Exception e) {
            // Nếu có lỗi, trả về giá trị mặc định
            stats.put("pendingBookings", 0);
            stats.put("upcomingTrips", 0);
            stats.put("totalCustomers", 0);
            stats.put("activeTours", 0);
        }
        
        return stats;
    }

    public Map<String, Object> getWeekRevenue() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            LocalDate today = LocalDate.now();
            List<String> labels = Arrays.asList("CN", "T2", "T3", "T4", "T5", "T6", "T7");
            List<BigDecimal> values = new ArrayList<>();
            
            // Lấy doanh thu 7 ngày gần đây
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                BigDecimal revenue = thanhToanRepository.getRevenueByDate(date);
                values.add(revenue != null ? revenue : BigDecimal.ZERO);
            }
            
            result.put("labels", labels);
            result.put("values", values);
            
        } catch (Exception e) {
            // Nếu có lỗi, trả về dữ liệu mặc định
            result.put("labels", Arrays.asList("CN", "T2", "T3", "T4", "T5", "T6", "T7"));
            result.put("values", Arrays.asList(0, 0, 0, 0, 0, 0, 0));
        }
        
        return result;
    }
}
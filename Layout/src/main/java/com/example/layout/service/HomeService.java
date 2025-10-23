package com.example.layout.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.KhachHangRepository;
import com.example.layout.repository.NhanvienRepository;
import com.example.layout.repository.ThanhToanRepository;

@Service
public class HomeService {

    @Autowired
    private ChuyenDuLichRepository chuyenDuLichRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private NhanvienRepository nhanvienRepository;
    
    @Autowired
    private ThanhToanRepository thanhToanRepository;

    public long getSoChuyenDangDienRa() {
        LocalDate today = LocalDate.now();
        return chuyenDuLichRepository.countChuyenDangDienRa();
    }

    public long getSoKhachHangMoi() {
        LocalDate today = LocalDate.now();
        return khachHangRepository.countKhachHangMoi(today);
    }

    public long getSoNhanVien() {
        return nhanvienRepository.count();
    }

    /**
     * Tổng doanh thu trong tháng hiện tại (theo ngày thanh toán)
     * @return tổng doanh thu (VND) trong BigDecimal
     */
    public java.math.BigDecimal getDoanhThuThang() {
        java.time.LocalDate today = java.time.LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        return thanhToanRepository.sumSoTienByMonthAndYear(month, year);
    }
    
    /**
     * Lấy doanh thu theo từng tháng trong năm
     * @return Map với key là tên tháng và value là doanh thu
     */
    public Map<String, Object> getDoanhThuTheoThang() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        
        List<Object[]> results = thanhToanRepository.getMonthlyRevenueByYear(year);
        Map<String, Object> monthlyRevenue = new HashMap<>();
        
        // Khởi tạo tất cả các tháng với giá trị 0
        String[] monthNames = {"T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12"};
        for (String monthName : monthNames) {
            monthlyRevenue.put(monthName, 0);
        }
        
        // Điền dữ liệu thực tế
        for (Object[] result : results) {
            Integer month = (Integer) result[0];
            java.math.BigDecimal revenue = (java.math.BigDecimal) result[1];
            monthlyRevenue.put("T" + month, revenue.doubleValue());
        }
        
        return monthlyRevenue;
    }
}

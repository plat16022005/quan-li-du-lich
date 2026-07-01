package com.example.layout.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.KhachHangRepository;
import com.example.layout.repository.NhanvienRepository;
import com.example.layout.repository.ThanhToanRepository;

@Service
public class HomeService implements IHomeService {

    private final ChuyenDuLichRepository chuyenDuLichRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanvienRepository nhanvienRepository;
    private final ThanhToanRepository thanhToanRepository;

    public HomeService(ChuyenDuLichRepository chuyenDuLichRepository,
                       KhachHangRepository khachHangRepository,
                       NhanvienRepository nhanvienRepository,
                       ThanhToanRepository thanhToanRepository) {
        this.chuyenDuLichRepository = chuyenDuLichRepository;
        this.khachHangRepository = khachHangRepository;
        this.nhanvienRepository = nhanvienRepository;
        this.thanhToanRepository = thanhToanRepository;
    }

    @Override
    public long getSoChuyenDangDienRa() {
        return chuyenDuLichRepository.countChuyenDangDienRa();
    }

    @Override
    public long getSoKhachHangMoi() {
        return khachHangRepository.countKhachHangMoi(LocalDate.now());
    }

    @Override
    public long getSoNhanVien() {
        return nhanvienRepository.count();
    }

    @Override
    public BigDecimal getDoanhThuThang() {
        LocalDate today = LocalDate.now();
        return thanhToanRepository.sumSoTienByMonthAndYear(today.getMonthValue(), today.getYear());
    }

    @Override
    public Map<String, Object> getDoanhThuTheoThang() {
        int year = LocalDate.now().getYear();
        List<Object[]> results = thanhToanRepository.getMonthlyRevenueByYear(year);
        Map<String, Object> monthlyRevenue = new HashMap<>();

        String[] monthNames = {"T1","T2","T3","T4","T5","T6","T7","T8","T9","T10","T11","T12"};
        for (String monthName : monthNames) {
            monthlyRevenue.put(monthName, 0);
        }

        for (Object[] result : results) {
            Integer month = (Integer) result[0];
            BigDecimal revenue = (BigDecimal) result[1];
            monthlyRevenue.put("T" + month, revenue.doubleValue());
        }

        return monthlyRevenue;
    }
}

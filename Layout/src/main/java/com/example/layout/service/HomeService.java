package com.example.layout.service;

import java.time.LocalDate;

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
}

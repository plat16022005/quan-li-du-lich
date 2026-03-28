package com.example.layout.service;

import com.example.layout.entity.Nhanvien;
import com.example.layout.entity.User;
import com.example.layout.entity.Vaitro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface INhanVienService {
    List<Nhanvien> getAllNhanVien();
    Optional<Nhanvien> findById(Integer maNhanVien);
    Nhanvien save(Nhanvien nhanvien);
    void deleteById(Integer maNhanVien);
    Page<Nhanvien> searchStaff(String keyword, Integer role, Boolean status, Pageable pageable);
    List<Nhanvien> getallHuongDanVien();
    List<Nhanvien> getallTaiXe();
    List<Nhanvien> getAvailableStaff(Vaitro role, LocalDate startDate, LocalDate endDate);
    User findTaiKhoanByUsername(String username);
    Nhanvien findByMaTaiKhoan(Integer maTaiKhoan);
}

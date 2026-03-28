package com.example.layout.service;

import com.example.layout.entity.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IKhachHangService {
    List<KhachHang> findAll();
    Page<KhachHang> findAll(Pageable pageable);
    Optional<KhachHang> findById(Integer id);
    KhachHang save(KhachHang khachHang);
    void deleteById(Integer id);
    List<KhachHang> search(String keyword);
    Page<KhachHang> search(String keyword, Pageable pageable);
    Optional<KhachHang> findByTaiKhoanId(Integer maTaiKhoan);
    List<KhachHang> getAllKhachHang();
    KhachHang getKhachHangById(Integer id);
    KhachHang getKhachHangByTaiKhoan(Integer maTaiKhoan);
    void deleteKhachHang(Integer id);
    KhachHang updateKhachHang(Integer id, KhachHang khachHangDetails);
    Map<String, Long> getMarketingStats();
    long getNewCustomersCount();
}

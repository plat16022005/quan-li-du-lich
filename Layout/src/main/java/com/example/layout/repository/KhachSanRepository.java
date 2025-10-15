package com.example.layout.repository;

import com.example.layout.entity.KhachSan;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KhachSanRepository extends JpaRepository<KhachSan, Integer> {
    List<KhachSan> findByTenKhachSan(String tenKhachSan);
    List<KhachSan> findByTenKhachSanContainingIgnoreCase(String keyword);
    List<KhachSan> findByDiaChiContainingIgnoreCase(String keyword);
    List<KhachSan> findByGiaTheoNgayBetween(double minPrice, double maxPrice);
}

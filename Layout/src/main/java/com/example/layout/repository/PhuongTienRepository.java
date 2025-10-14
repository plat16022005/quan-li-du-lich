package com.example.layout.repository;

import com.example.layout.entity.PhuongTien;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhuongTienRepository extends JpaRepository<PhuongTien, Integer> {
    List<PhuongTien> findByLoaiPhuongTien(String loaiPhuongTien);
    List<PhuongTien> findBySoChoNgoiGreaterThanEqual(int soChoNgoi);
}

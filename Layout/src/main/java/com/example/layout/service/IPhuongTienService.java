package com.example.layout.service;

import com.example.layout.entity.PhuongTien;

import java.util.List;
import java.util.Optional;

public interface IPhuongTienService {
    List<PhuongTien> findAll();
    Optional<PhuongTien> findById(Integer maPhuongTien);
    PhuongTien save(PhuongTien phuongTien);
    void deleteById(Integer maPhuongTien);
    List<PhuongTien> findByLoai(String loaiPhuongTien);
    List<PhuongTien> findBySoChoNgoi(int soCho);
}

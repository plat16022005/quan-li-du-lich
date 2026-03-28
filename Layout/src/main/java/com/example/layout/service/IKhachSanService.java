package com.example.layout.service;

import com.example.layout.entity.KhachSan;

import java.util.List;
import java.util.Optional;

public interface IKhachSanService {
    List<KhachSan> findAll();
    Optional<KhachSan> findById(Integer maKhachSan);
    KhachSan save(KhachSan khachSan);
    void deleteById(Integer maKhachSan);
    List<KhachSan> searchByTen(String keyword);
    List<KhachSan> findByDiaDiem(Integer maDiaDiem);
    List<KhachSan> findByPriceRange(double minPrice, double maxPrice);
}

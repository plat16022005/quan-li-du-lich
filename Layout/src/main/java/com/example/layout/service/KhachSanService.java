package com.example.layout.service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.example.layout.repository.KhachSanRepository;
import com.example.layout.entity.*;

@Service
public class KhachSanService {
    private final KhachSanRepository khachSanRepository;

    public KhachSanService(KhachSanRepository khachSanRepository){
        this.khachSanRepository = khachSanRepository;
    }

    public List<KhachSan> findAll(){
        return khachSanRepository.findAll();
    }

    public Optional<KhachSan> findById(Integer maKhachSan){
        return khachSanRepository.findById(maKhachSan);
    }

    public KhachSan save(KhachSan khachSan){
        return khachSanRepository.save(khachSan);
    }

    public void deleteById(Integer maKhachSan){
        khachSanRepository.deleteById(maKhachSan);
    }

    public List<KhachSan> searchByTen(String keyword){
        return khachSanRepository.findByTenKhachSanContainingIgnoreCase(keyword);
    }

    public List<KhachSan> findByDiaDiem(Integer maDiaDiem) {
        return khachSanRepository.findByDiaDiem(maDiaDiem);
    }

    public List<KhachSan> findByPriceRange(double minPrice, double maxPrice) {
        return khachSanRepository.findByGiaTheoNgayBetween(minPrice, maxPrice);
    }
}

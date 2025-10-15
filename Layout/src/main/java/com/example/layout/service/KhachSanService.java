package com.example.layout.service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.example.layout.repository.DiaDiemRepository;
import com.example.layout.repository.KhachSanRepository;
import com.example.layout.entity.*;

@Service
public class KhachSanService {
    private final KhachSanRepository khachSanRepository;
    private final DiaDiemRepository diaDiemRepository;

    public KhachSanService(KhachSanRepository khachSanRepository, DiaDiemRepository diaDiemRepository, DiaDiemService diaDiemService) {
        this.khachSanRepository = khachSanRepository;
        this.diaDiemRepository = diaDiemRepository;
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
        DiaDiem diaDiem = diaDiemRepository.findById(maDiaDiem)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa điểm"));
        
        String tenDiaDiem = diaDiem.getTenDiaDiem();

        // 2. Dùng tên địa điểm để tìm kiếm trong địa chỉ của khách sạn
        return khachSanRepository.findByDiaChiContainingIgnoreCase(tenDiaDiem);
    }

    public List<KhachSan> findByPriceRange(double minPrice, double maxPrice) {
        return khachSanRepository.findByGiaTheoNgayBetween(minPrice, maxPrice);
    }
}

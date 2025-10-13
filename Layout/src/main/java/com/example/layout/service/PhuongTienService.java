package com.example.layout.service;

import org.springframework.stereotype.Service;

import com.example.layout.entity.PhuongTien;
import com.example.layout.repository.PhuongTienRepository;

import java.util.*;

@Service
public class PhuongTienService {
    private final PhuongTienRepository phuongTienRepository;

    public PhuongTienService(PhuongTienRepository phuongTienRepository)
    {
        this.phuongTienRepository = phuongTienRepository;
    }

    public List<PhuongTien> findAll(){
        return phuongTienRepository.findAll();
    }

    public Optional<PhuongTien> findById(Integer maPhuongTien){
        return phuongTienRepository.findById(maPhuongTien);
    }

    public PhuongTien save(PhuongTien phuongTien){
        return phuongTienRepository.save(phuongTien);
    }

    public void deleteById(Integer maPhuongTien){
        phuongTienRepository.deleteById(maPhuongTien);
    }

    public List<PhuongTien> findByLoai(String loaiPhuongTien) {
        return phuongTienRepository.findByLoaiPhuongTien(loaiPhuongTien);
    }

    public List<PhuongTien> findBySoChoNgoi(int soCho) {
        return phuongTienRepository.findBySoChoNgoiGreaterThanEqual(soCho);
    }
}

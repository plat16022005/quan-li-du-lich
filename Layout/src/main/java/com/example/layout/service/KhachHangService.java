package com.example.layout.service;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.layout.entity.KhachHang;
import com.example.layout.repository.KhachHangRepository;

@Service
public class KhachHangService {
    private final KhachHangRepository khachHangRepository;

    public KhachHangService(KhachHangRepository khachHangRepository) {
        this.khachHangRepository = khachHangRepository;
    }

    public List<KhachHang> findAll() {
        return khachHangRepository.findAll();
    }
    public Page<KhachHang> findAll(Pageable pageable){
    	return khachHangRepository.findAll(pageable);
    }

    public Optional<KhachHang> findById(Integer id) {
        return khachHangRepository.findById(id);
    }

    public KhachHang save(KhachHang khachHang) {
        return khachHangRepository.save(khachHang);
    }

    public void deleteById(Integer id) {
        khachHangRepository.deleteById(id);
    }

    public List<KhachHang> search(String keyword) {
        return khachHangRepository.search(keyword);
    }

    public Optional<KhachHang> findByTaiKhoanId(Integer maTaiKhoan) {
        return Optional.ofNullable(khachHangRepository.findByTaiKhoan_MaTaiKhoan(maTaiKhoan));
    }
    
    public Page<KhachHang> search(String keyword, Pageable pageable) {
        return khachHangRepository.search(keyword, pageable);
    }

}

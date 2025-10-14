package com.example.layout.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.KhachHangRepository;
import com.example.layout.repository.NhanvienRepository;

@Service
public class HomeService {

    @Autowired
    private ChuyenDuLichRepository chuyenDuLichRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private NhanvienRepository nhanvienRepository;

    public long getSoChuyenDangDienRa() {
        LocalDate today = LocalDate.now();
        return chuyenDuLichRepository.countChuyenDangDienRa();
    }

    public long getSoKhachHangMoi() {
        LocalDate today = LocalDate.now();
        return khachHangRepository.countKhachHangMoi(today);
    }

    public long getSoNhanVien() {
        return nhanvienRepository.count();
    }
}

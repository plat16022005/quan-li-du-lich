package com.example.layout.service;

import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.Nhanvien;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.NhanvienRepository;

import org.springframework.data.domain.*;
import com.example.layout.service.NhanVienService;

//import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
//@RequiredArgsConstructor
public class ChuyenDuLichService {
    private final ChuyenDuLichRepository chuyenDuLichRepository;
    private final NhanVienService nhanVienService;

    public ChuyenDuLichService(ChuyenDuLichRepository chuyenDuLichRepository, NhanVienService nhanVienService) {
        this.chuyenDuLichRepository = chuyenDuLichRepository;
        this.nhanVienService = nhanVienService;
    }

    public Optional<ChuyenDuLich> getNearestChuyen(Integer maTour) {
        return chuyenDuLichRepository.findTopByTour_MaTourOrderByNgayBatDauAsc(maTour);
    }
    
    public int getTotalParticipants(Integer maChuyen) {
        return chuyenDuLichRepository.getTotalParticipants(maChuyen);
    }
    public ChuyenDuLich saveChuyen(ChuyenDuLich chuyen) {
        return chuyenDuLichRepository.save(chuyen);
    }
    public ChuyenDuLich getChuyenById(Integer maChuyen)
    {
        return chuyenDuLichRepository.findById(maChuyen).orElse(null);
    }

    public Page<ChuyenDuLich> findAll(Pageable pageable) {
        return chuyenDuLichRepository.findAll(pageable);
    }


    public List<ChuyenDuLich> findByTrangThai(String trangThai) {
        return chuyenDuLichRepository.findByTrangThai(trangThai);
    }


    public void deleteById(Integer maChuyen) {
        chuyenDuLichRepository.deleteById(maChuyen);
    }


    public ChuyenDuLich updateStatus(Integer maChuyen, String newStatus) {
        ChuyenDuLich chuyen = getChuyenById(maChuyen);
        if (chuyen != null) {
            chuyen.setTrangThai(newStatus);
            return chuyenDuLichRepository.save(chuyen);
        }
        throw new RuntimeException("Không tìm thấy chuyến du lịch với ID: " + maChuyen);
    }

    public ChuyenDuLich assignStaff(Integer maTaiXe, Integer maHuongDanVien, Integer maChuyen) {
        ChuyenDuLich chuyen = chuyenDuLichRepository.findById(maChuyen)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến du lịch với ID: " + maChuyen));

        Nhanvien hdv = nhanVienService.findById(maHuongDanVien)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Hướng dẫn viên với ID: " + maHuongDanVien));

        Nhanvien taixe = nhanVienService.findById(maTaiXe)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài xế với ID: " + maTaiXe));
        
        chuyen.setHuongDanVien(hdv);
        chuyen.setTaiXe(taixe);

        return chuyenDuLichRepository.save(chuyen);
    }
}

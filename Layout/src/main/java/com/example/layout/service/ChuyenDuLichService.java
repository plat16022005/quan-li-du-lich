package com.example.layout.service;

import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.Nhanvien;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.NhanvienRepository;

import org.springframework.data.domain.*;
import com.example.layout.service.NhanVienService;

//import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
//@RequiredArgsConstructor
public class ChuyenDuLichService {
    private final ChuyenDuLichRepository chuyenDuLichRepository;
    private final NhanVienService nhanVienService;
    private NhanvienRepository nhanVienRepository;

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

    public List<ChuyenDuLich> getAvailableTripsByRole(int maVaiTro) {
        if (maVaiTro == 3) { 
            return chuyenDuLichRepository.findByHuongDanVienIsNullAndTrangThai("Sắp diễn ra");
        } else if (maVaiTro == 5) {
            return chuyenDuLichRepository.findByHuongDanVienIsNullAndTrangThai("Sắp diễn ra");
        }
        return Collections.emptyList();
    }

    public List<ChuyenDuLich> getAssignedTripsByEmployee(int maNhanVien, int maVaiTro) {
        if (maVaiTro == 3) {
            return chuyenDuLichRepository.findByHuongDanVien_MaNhanVien(maNhanVien);
        } else if (maVaiTro == 5) {
            return chuyenDuLichRepository.findByHuongDanVien_MaNhanVien(maNhanVien);
        }
        return Collections.emptyList();
    }

    public void assignTripToEmployee(int maChuyen, int maNhanVien, int maVaiTro) {
        ChuyenDuLich chuyen = chuyenDuLichRepository.findById(maChuyen)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi với mã: " + maChuyen));

        Nhanvien nhanVien = nhanVienRepository.findById(maNhanVien)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với mã: " + maNhanVien));

        if (maVaiTro == 3) {
            if (chuyen.getHuongDanVien() != null) {
                throw new IllegalStateException("Chuyến đi này đã có Hướng dẫn viên nhận.");
            }
            chuyen.setHuongDanVien(nhanVien);
        } else if (maVaiTro == 5) {
            if (chuyen.getTaiXe() != null) {
                throw new IllegalStateException("Chuyến đi này đã có Tài xế nhận.");
            }
            chuyen.setTaiXe(nhanVien);
        }
        chuyenDuLichRepository.save(chuyen);
    }
}

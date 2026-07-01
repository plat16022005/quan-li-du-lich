package com.example.layout.service.impl;


import com.example.layout.controller.hdvtx.HdvTxDashboardController.CurrentUserDTO;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.Nhanvien;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.NhanvienRepository;
import com.example.layout.service.HdvTxService;
import com.example.layout.utils.TripStatusChecker;
import com.example.layout.utils.VaiTroConstants;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class HdvTxServiceImpl implements HdvTxService {

    private final ChuyenDuLichRepository chuyenDuLichRepository;
    private final NhanvienRepository nhanvienRepository;

    private static final Logger logger = LoggerFactory.getLogger(HdvTxServiceImpl.class);

    public HdvTxServiceImpl(ChuyenDuLichRepository chuyenDuLichRepository,
                            NhanvienRepository nhanvienRepository) {
        this.chuyenDuLichRepository = chuyenDuLichRepository;
        this.nhanvienRepository = nhanvienRepository;
    }

    public List<ChuyenDuLich> getAvailableTrips(CurrentUserDTO currentUser) {
        String trangThai = "Sắp diễn ra";
        if (currentUser.getMaVaiTro() == VaiTroConstants.HUONG_DAN_VIEN) {
            return chuyenDuLichRepository.findByHuongDanVienIsNullAndTrangThai(trangThai);
        } else if (currentUser.getMaVaiTro() == VaiTroConstants.TAI_XE) {
            return chuyenDuLichRepository.findByTaiXeIsNullAndTrangThai(trangThai);
        }
        return Collections.emptyList();
    }


    public List<ChuyenDuLich> getAssignedTrips(CurrentUserDTO currentUser) {
        if (currentUser.getMaVaiTro() == VaiTroConstants.HUONG_DAN_VIEN) {
            return chuyenDuLichRepository.findByHuongDanVien_MaNhanVien(currentUser.getMaNhanVien());
        } else if (currentUser.getMaVaiTro() == VaiTroConstants.TAI_XE) {
            return chuyenDuLichRepository.findByTaiXe_MaNhanVien(currentUser.getMaNhanVien());
        }
        return Collections.emptyList();
    }
    public Set<Integer> findConflictingTripIds(List<ChuyenDuLich> trips) {
        Set<Integer> conflictIds = new HashSet<>();
        
        for (int i = 0; i < trips.size(); i++) {
            ChuyenDuLich trip1 = trips.get(i);
            if (trip1.getNgayBatDau() == null || trip1.getNgayKetThuc() == null) {
                continue;
            }
            
            for (int j = i + 1; j < trips.size(); j++) {
                ChuyenDuLich trip2 = trips.get(j);
                if (trip2.getNgayBatDau() == null || trip2.getNgayKetThuc() == null) {
                    continue;
                }
                
                // Kiểm tra overlap
                boolean hasOverlap = !(
                    trip1.getNgayKetThuc().isBefore(trip2.getNgayBatDau()) ||
                    trip1.getNgayBatDau().isAfter(trip2.getNgayKetThuc())
                );
                
                if (hasOverlap) {
                    logger.warn("⚠️ Phát hiện trùng lịch: Chuyến {} ({} - {}) <=> Chuyến {} ({} - {})",
                        trip1.getMaChuyen(), trip1.getNgayBatDau(), trip1.getNgayKetThuc(),
                        trip2.getMaChuyen(), trip2.getNgayBatDau(), trip2.getNgayKetThuc()
                    );
                    
                    conflictIds.add(trip1.getMaChuyen());
                    conflictIds.add(trip2.getMaChuyen());
                }
            }
        }
        
        return conflictIds;
    }

    @Override
    @Transactional
    public void assignTripToEmployee(int tripId, CurrentUserDTO currentUser) {
        ChuyenDuLich chuyen = chuyenDuLichRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi!"));
        
        Nhanvien nhanvien = nhanvienRepository.findById(currentUser.getMaNhanVien())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên!"));

        if (currentUser.getMaVaiTro() == VaiTroConstants.HUONG_DAN_VIEN) {
            if (chuyen.getHuongDanVien() != null) {
                throw new IllegalStateException("Chuyến đi này đã có Hướng dẫn viên phụ trách!");
            }
            chuyen.setHuongDanVien(nhanvien);
        } else if (currentUser.getMaVaiTro() == VaiTroConstants.TAI_XE) {
            if (chuyen.getTaiXe() != null) {
                throw new IllegalStateException("Chuyến đi này đã có Tài xế phụ trách!");
            }
            chuyen.setTaiXe(nhanvien);
        } else {
            throw new IllegalStateException("Bạn không có quyền nhận chuyến đi này!");
        }
        
        chuyenDuLichRepository.save(chuyen);
    }
    
    @Override
    @Transactional
    public void cancelTripAssignment(int tripId, CurrentUserDTO currentUser) {
        ChuyenDuLich chuyen = chuyenDuLichRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi!"));

        // ✅ Chặn hủy nếu chuyến đang diễn ra hoặc đã hoàn thành
        String trangThai = chuyen.getTrangThai();
        if (TripStatusChecker.isNonCancellable(trangThai)) {
            throw new IllegalStateException("Không thể hủy chuyến đi đang diễn ra hoặc đã hoàn thành!");
        }

        if (currentUser.getMaVaiTro() == VaiTroConstants.HUONG_DAN_VIEN
            && chuyen.getHuongDanVien() != null
            && chuyen.getHuongDanVien().getMaNhanVien().equals(currentUser.getMaNhanVien())) {
            chuyen.setHuongDanVien(null);
        } else if (currentUser.getMaVaiTro() == VaiTroConstants.TAI_XE
                   && chuyen.getTaiXe() != null
                   && chuyen.getTaiXe().getMaNhanVien().equals(currentUser.getMaNhanVien())) {
            chuyen.setTaiXe(null);
        } else {
            throw new IllegalStateException("Bạn không thể hủy chuyến đi này.");
        }
        
        chuyenDuLichRepository.save(chuyen);
        logger.info("✅ Đã hủy nhận chuyến {}", tripId);
    }
    
    @Override
    public List<ChuyenDuLich> searchAssignedTrips(
            CurrentUserDTO currentUser, 
            String trangThai, 
            String tenTour, 
            LocalDate tuNgay, 
            LocalDate denNgay) {
        
        return chuyenDuLichRepository.searchAssignedTrips(
            currentUser.getMaNhanVien(),
            trangThai,
            tenTour,
            tuNgay,
            denNgay
        );
    }

    @Override
    public List<ChuyenDuLich> searchAvailableTrips(
            CurrentUserDTO currentUser, 
            String tenTour, 
            LocalDate tuNgay, 
            LocalDate denNgay) {
        
        return chuyenDuLichRepository.searchAvailableTrips(
            currentUser.getMaVaiTro(),
            tenTour,
            tuNgay,
            denNgay
        );
    }
}
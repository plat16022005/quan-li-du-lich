package com.example.layout.service.impl;


import com.example.layout.controller.hdvtx.HdvTxDashboardController.CurrentUserDTO;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.Nhanvien;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.NhanvienRepository;
import com.example.layout.service.HdvTxService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class HdvTxServiceImpl implements HdvTxService {

    @Autowired
    private ChuyenDuLichRepository chuyenDuLichRepository;
    @Autowired
    private NhanvienRepository nhanvienRepository;

    public List<ChuyenDuLich> getAvailableTrips(CurrentUserDTO currentUser) {
        String trangThai = "Sắp diễn ra";
        if (currentUser.getMaVaiTro() == 3) { // 3 là Hướng dẫn viên
            return chuyenDuLichRepository.findByHuongDanVienIsNullAndTrangThai(trangThai);
        } else if (currentUser.getMaVaiTro() == 5) { // 5 là Tài xế
            return chuyenDuLichRepository.findByTaiXeIsNullAndTrangThai(trangThai);
        }
        return Collections.emptyList();
    }


    public List<ChuyenDuLich> getAssignedTrips(CurrentUserDTO currentUser) {
        if (currentUser.getMaVaiTro() == 3) {
            return chuyenDuLichRepository.findByHuongDanVien_MaNhanVien(currentUser.getMaNhanVien());
        } else if (currentUser.getMaVaiTro() == 5) {
            return chuyenDuLichRepository.findByTaiXe_MaNhanVien(currentUser.getMaNhanVien());
        }
        return Collections.emptyList();
    }

    public void assignTripToEmployee(int tripId, CurrentUserDTO currentUser) {
        ChuyenDuLich chuyen = chuyenDuLichRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi!"));
        Nhanvien nhanvien = nhanvienRepository.findById(currentUser.getMaNhanVien())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên!"));

        if (currentUser.getMaVaiTro() == 3) {
            if (chuyen.getHuongDanVien() != null) throw new IllegalStateException("Chuyến đã có HDV!");
            chuyen.setHuongDanVien(nhanvien);
        } else if (currentUser.getMaVaiTro() == 5) {
            if (chuyen.getTaiXe() != null) throw new IllegalStateException("Chuyến đã có Tài xế!");
            chuyen.setTaiXe(nhanvien);
        }
        chuyenDuLichRepository.save(chuyen);
    }

    public void cancelTripAssignment(int tripId, CurrentUserDTO currentUser) {
        ChuyenDuLich chuyen = chuyenDuLichRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi!"));

        if (currentUser.getMaVaiTro() == 3 && chuyen.getHuongDanVien() != null && chuyen.getHuongDanVien().getMaNhanVien() == currentUser.getMaNhanVien()) {
            chuyen.setHuongDanVien(null);
        } else if (currentUser.getMaVaiTro() == 5 && chuyen.getTaiXe() != null && chuyen.getTaiXe().getMaNhanVien() == currentUser.getMaNhanVien()) {
            chuyen.setTaiXe(null);
        } else {
            throw new IllegalStateException("Bạn không thể hủy chuyến đi này.");
        }
        chuyenDuLichRepository.save(chuyen);
    }

}
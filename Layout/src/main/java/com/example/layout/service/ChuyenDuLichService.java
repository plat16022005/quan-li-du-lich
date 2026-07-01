package com.example.layout.service;

import com.example.layout.dto.HanhKhachDTO;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.Nhanvien;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.DatChoRepository;
import com.example.layout.repository.KhachHangRepository;
import com.example.layout.repository.NhanvienRepository;
import com.example.layout.utils.VaiTroConstants;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ChuyenDuLichService implements IChuyenDuLichService {
    private final ChuyenDuLichRepository chuyenDuLichRepository;
    private final NhanvienRepository nhanVienRepository;
    private final DatChoRepository datChoRepository;
    private final INhanVienService nhanVienService;
    private final KhachHangRepository khachHangRepository;

    public ChuyenDuLichService(ChuyenDuLichRepository chuyenDuLichRepository,
                               INhanVienService nhanVienService,
                               NhanvienRepository nhanVienRepository,
                               DatChoRepository datChoRepository,
                               KhachHangRepository khachHangRepository) {
        this.chuyenDuLichRepository = chuyenDuLichRepository;
        this.nhanVienService        = nhanVienService;
        this.nhanVienRepository     = nhanVienRepository;
        this.datChoRepository       = datChoRepository;
        this.khachHangRepository    = khachHangRepository;
    }

    // === CÁC PHƯƠNG THỨC CƠ BẢN ===

    @Override
    public Optional<ChuyenDuLich> getNearestChuyen(Integer maTour) {
        return chuyenDuLichRepository.findTopByTour_MaTourOrderByNgayBatDauAsc(maTour);
    }
    
    @Override
    public int getTotalParticipants(Integer maChuyen) {
        return chuyenDuLichRepository.getTotalParticipants(maChuyen);
    }

    @Override
    public ChuyenDuLich saveChuyen(ChuyenDuLich chuyen) {
        return chuyenDuLichRepository.save(chuyen);
    }

    @Override
    public ChuyenDuLich getChuyenById(Integer maChuyen) {
        return chuyenDuLichRepository.findById(maChuyen).orElse(null);
    }

    @Override
    public Page<ChuyenDuLich> findAll(Pageable pageable) {
        return chuyenDuLichRepository.findAll(pageable);
    }

    @Override
    public List<ChuyenDuLich> findAlltrips() {
        return chuyenDuLichRepository.findAllWithTour();
    }

    @Override
    public List<ChuyenDuLich> findAllCompletedTrips() {
        return chuyenDuLichRepository.findAllCompletedTrips();
    }

    @Override
    public List<ChuyenDuLich> findByTrangThai(String trangThai) {
        return chuyenDuLichRepository.findByTrangThai(trangThai);
    }

    @Override
    public void deleteById(Integer maChuyen) {
        chuyenDuLichRepository.deleteById(maChuyen);
    }

    @Override
    public List<ChuyenDuLich> findByTour_MaTour(Integer maTour) {
        return chuyenDuLichRepository.findByTour_MaTour(maTour);
    }

    @Override
    public Page<ChuyenDuLich> findByTour_MaTour(Integer maTour, Pageable pageable) {
        return chuyenDuLichRepository.findByTour_MaTour(maTour, pageable);
    }

    @Override
    public ChuyenDuLich updateStatus(Integer maChuyen, String newStatus) {
        ChuyenDuLich chuyen = getChuyenById(maChuyen);
        if (chuyen != null) {
            chuyen.setTrangThai(newStatus);
            return chuyenDuLichRepository.save(chuyen);
        }
        throw new RuntimeException("Không tìm thấy chuyến du lịch với ID: " + maChuyen);
    }

    @Override
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

    @Override
    public List<ChuyenDuLich> getAvailableTripsByRole(int maVaiTro) {
        if (maVaiTro == VaiTroConstants.HUONG_DAN_VIEN || maVaiTro == VaiTroConstants.TAI_XE) {
            return chuyenDuLichRepository.findByHuongDanVienIsNullAndTrangThai("Sắp diễn ra");
        }
        return Collections.emptyList();
    }

    @Override
    public List<ChuyenDuLich> getAssignedTripsByEmployee(int maNhanVien, int maVaiTro) {
        if (maVaiTro == VaiTroConstants.HUONG_DAN_VIEN || maVaiTro == VaiTroConstants.TAI_XE) {
            return chuyenDuLichRepository.findByHuongDanVien_MaNhanVien(maNhanVien);
        }
        return Collections.emptyList();
    }

    @Override
    public void assignTripToEmployee(int maChuyen, int maNhanVien, int maVaiTro) {
        ChuyenDuLich chuyen = chuyenDuLichRepository.findById(maChuyen)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi với mã: " + maChuyen));

        Nhanvien nhanVien = nhanVienRepository.findById(maNhanVien)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với mã: " + maNhanVien));

        if (maVaiTro == VaiTroConstants.HUONG_DAN_VIEN) {
            if (chuyen.getHuongDanVien() != null) {
                throw new IllegalStateException("Chuyến đi này đã có Hướng dẫn viên nhận.");
            }
            chuyen.setHuongDanVien(nhanVien);
        } else if (maVaiTro == VaiTroConstants.TAI_XE) {
            if (chuyen.getTaiXe() != null) {
                throw new IllegalStateException("Chuyến đi này đã có Tài xế nhận.");
            }
            chuyen.setTaiXe(nhanVien);
        }
        chuyenDuLichRepository.save(chuyen);
    }

    @Override
    public ChuyenDuLich getChuyenWithSoLuongHienTai(Integer maChuyen) {
        ChuyenDuLich chuyen = chuyenDuLichRepository.findById(maChuyen)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến"));
        int soLuongHienTai = datChoRepository.getTongSoLuongDaDat(maChuyen);
        chuyen.setSoLuongHienTai(soLuongHienTai);
        return chuyen;
    }

    @Override
    public List<ChuyenDuLich> getAllChuyenWithSoLuongHienTai() {
        List<ChuyenDuLich> danhSach = chuyenDuLichRepository.findAll();
        danhSach.forEach(c -> {
            int daDat = datChoRepository.getTongSoLuongDaDat(c.getMaChuyen());
            c.setSoLuongHienTai(daDat);
        });
        return danhSach;
    }

    @Override
    public List<HanhKhachDTO> getHanhKhachByMaChuyen(Integer maChuyen) {
        return khachHangRepository.findHanhKhachByMaChuyen(maChuyen);
    }
}
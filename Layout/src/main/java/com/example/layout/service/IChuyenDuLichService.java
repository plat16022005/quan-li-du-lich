package com.example.layout.service;

import com.example.layout.dto.HanhKhachDTO;
import com.example.layout.entity.ChuyenDuLich;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IChuyenDuLichService {
    Optional<ChuyenDuLich> getNearestChuyen(Integer maTour);
    int getTotalParticipants(Integer maChuyen);
    ChuyenDuLich saveChuyen(ChuyenDuLich chuyen);
    ChuyenDuLich getChuyenById(Integer maChuyen);
    Page<ChuyenDuLich> findAll(Pageable pageable);
    List<ChuyenDuLich> findAlltrips();
    List<ChuyenDuLich> findAllCompletedTrips();
    List<ChuyenDuLich> findByTrangThai(String trangThai);
    void deleteById(Integer maChuyen);
    List<ChuyenDuLich> findByTour_MaTour(Integer maTour);
    Page<ChuyenDuLich> findByTour_MaTour(Integer maTour, Pageable pageable);
    ChuyenDuLich updateStatus(Integer maChuyen, String newStatus);
    ChuyenDuLich assignStaff(Integer maTaiXe, Integer maHuongDanVien, Integer maChuyen);
    List<ChuyenDuLich> getAvailableTripsByRole(int maVaiTro);
    List<ChuyenDuLich> getAssignedTripsByEmployee(int maNhanVien, int maVaiTro);
    void assignTripToEmployee(int maChuyen, int maNhanVien, int maVaiTro);
    ChuyenDuLich getChuyenWithSoLuongHienTai(Integer maChuyen);
    List<ChuyenDuLich> getAllChuyenWithSoLuongHienTai();

    /**
     * Lấy danh sách hành khách của một chuyến đi.
     * Cho phép controller không cần inject KhachHangRepository trực tiếp (DIP).
     */
    List<HanhKhachDTO> getHanhKhachByMaChuyen(Integer maChuyen);
}

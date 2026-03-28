package com.example.layout.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.*;
import com.example.layout.entity.*;
import com.example.layout.repository.NhanvienRepository;
import com.example.layout.repository.UserRepository;
import com.example.layout.repository.VaiTroRepository;

@Service
public class NhanVienService implements INhanVienService {

    private final NhanvienRepository nhanvienRepository;
    private final VaiTroRepository vaiTroRepository;
    private final UserRepository userRepository;

    public NhanVienService(NhanvienRepository nhanvienRepository,
                           VaiTroRepository vaiTroRepository,
                           UserRepository userRepository) {
        this.nhanvienRepository = nhanvienRepository;
        this.vaiTroRepository = vaiTroRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Nhanvien> getAllNhanVien() {
        return nhanvienRepository.findAll();
    }

    @Override
    public Optional<Nhanvien> findById(Integer maNhanVien) {
        return nhanvienRepository.findById(maNhanVien);
    }

    @Override
    public Nhanvien save(Nhanvien nhanvien) {
        return nhanvienRepository.save(nhanvien);
    }

    @Override
    public void deleteById(Integer maNhanVien) {
        nhanvienRepository.deleteById(maNhanVien);
    }

    @Override
    public Page<Nhanvien> searchStaff(String keyword, Integer role, Boolean status, Pageable pageable) {
        return nhanvienRepository.searchStaff(keyword, role, status, pageable);
    }

    @Override
    public List<Nhanvien> getallHuongDanVien() {
        Vaitro vaiTro = vaiTroRepository.findByTenVaiTro("Hướng dẫn viên")
                .orElseThrow(() -> new RuntimeException("Hướng dẫn viên không tồn tại"));
        return nhanvienRepository.findByTaiKhoan_MaVaiTro(vaiTro.getMaVaiTro());
    }

    @Override
    public List<Nhanvien> getallTaiXe() {
        Vaitro vaiTro = vaiTroRepository.findByTenVaiTro("Tài xế")
                .orElseThrow(() -> new RuntimeException("Tài xế không tồn tại"));
        return nhanvienRepository.findByTaiKhoan_MaVaiTro(vaiTro.getMaVaiTro());
    }

    @Override
    public List<Nhanvien> getAvailableStaff(Vaitro role, LocalDate startDate, LocalDate endDate) {
        return nhanvienRepository.findAvailableStaff(role, startDate, endDate);
    }

    @Override
    public User findTaiKhoanByUsername(String username) {
        return userRepository.findByTenDangNhap(username);
    }

    @Override
    public Nhanvien findByMaTaiKhoan(Integer maTaiKhoan) {
        return nhanvienRepository.findByTaiKhoan_MaTaiKhoan(maTaiKhoan).orElse(null);
    }
}

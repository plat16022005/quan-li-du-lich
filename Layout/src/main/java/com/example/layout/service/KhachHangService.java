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

    public List<KhachHang> getAllKhachHang() {
        return khachHangRepository.findAll();
    }

    public KhachHang getKhachHangById(Integer id) {
        return khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
    }

    public KhachHang getKhachHangByTaiKhoan(Integer maTaiKhoan) {
        return khachHangRepository.findByTaiKhoan_MaTaiKhoan(maTaiKhoan);
    }

    public void deleteKhachHang(Integer id) {
        khachHangRepository.deleteById(id);
    }

    public KhachHang updateKhachHang(Integer id, KhachHang khachHangDetails) {
        KhachHang khachHang = getKhachHangById(id);
        
        khachHang.setDiaChi(khachHangDetails.getDiaChi());
        khachHang.setNgaySinh(khachHangDetails.getNgaySinh());
        khachHang.setGioiTinh(khachHangDetails.getGioiTinh());
        khachHang.setBietDen(khachHangDetails.getBietDen());
        
        return khachHangRepository.save(khachHang);
    }

    public Map<String, Long> getMarketingStats() {
        List<Object[]> stats = khachHangRepository.thongKeNguonKhachHang();
        return stats.stream()
                .collect(Collectors.toMap(
                        stat -> (String) stat[0],
                        stat -> (Long) stat[1]
                ));
    }

    public long getNewCustomersCount() {
        return khachHangRepository.countKhachHangMoi(LocalDate.now());
    }
}

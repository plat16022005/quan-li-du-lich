package com.example.layout.service;

import com.example.layout.entity.KhachHang;
import com.example.layout.repository.KhachHangRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KhachHangService implements IKhachHangService {

    private final KhachHangRepository khachHangRepository;

    public KhachHangService(KhachHangRepository khachHangRepository) {
        this.khachHangRepository = khachHangRepository;
    }

    @Override
    public List<KhachHang> findAll() {
        return khachHangRepository.findAll();
    }

    @Override
    public Page<KhachHang> findAll(Pageable pageable) {
        return khachHangRepository.findAll(pageable);
    }

    @Override
    public Optional<KhachHang> findById(Integer id) {
        return khachHangRepository.findById(id);
    }

    @Override
    public KhachHang save(KhachHang khachHang) {
        return khachHangRepository.save(khachHang);
    }

    @Override
    public void deleteById(Integer id) {
        khachHangRepository.deleteById(id);
    }

    @Override
    public List<KhachHang> search(String keyword) {
        return khachHangRepository.search(keyword);
    }

    @Override
    public Optional<KhachHang> findByTaiKhoanId(Integer maTaiKhoan) {
        return Optional.ofNullable(khachHangRepository.findByTaiKhoan_MaTaiKhoan(maTaiKhoan));
    }

    @Override
    public Page<KhachHang> search(String keyword, Pageable pageable) {
        return khachHangRepository.search(keyword, pageable);
    }

    @Override
    public List<KhachHang> getAllKhachHang() {
        return khachHangRepository.findAll();
    }

    @Override
    public KhachHang getKhachHangById(Integer id) {
        return khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
    }

    @Override
    public KhachHang getKhachHangByTaiKhoan(Integer maTaiKhoan) {
        return khachHangRepository.findByTaiKhoan_MaTaiKhoan(maTaiKhoan);
    }

    @Override
    public void deleteKhachHang(Integer id) {
        khachHangRepository.deleteById(id);
    }

    @Override
    public KhachHang updateKhachHang(Integer id, KhachHang khachHangDetails) {
        KhachHang khachHang = getKhachHangById(id);
        khachHang.setDiaChi(khachHangDetails.getDiaChi());
        khachHang.setNgaySinh(khachHangDetails.getNgaySinh());
        khachHang.setGioiTinh(khachHangDetails.getGioiTinh());
        khachHang.setBietDen(khachHangDetails.getBietDen());
        return khachHangRepository.save(khachHang);
    }

    @Override
    public Map<String, Long> getMarketingStats() {
        List<Object[]> stats = khachHangRepository.thongKeNguonKhachHang();
        return stats.stream()
                .collect(Collectors.toMap(
                        stat -> (String) stat[0],
                        stat -> (Long) stat[1]
                ));
    }

    @Override
    public long getNewCustomersCount() {
        return khachHangRepository.countKhachHangMoi(LocalDate.now());
    }
}

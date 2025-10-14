package com.example.layout.service;

import com.example.layout.entity.KhachHang;
import com.example.layout.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

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

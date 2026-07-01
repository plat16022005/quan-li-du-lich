package com.example.layout.service;

import java.math.BigDecimal;
import java.util.Map;

public interface IHomeService {
    long getSoChuyenDangDienRa();
    long getSoKhachHangMoi();
    long getSoNhanVien();
    BigDecimal getDoanhThuThang();
    Map<String, Object> getDoanhThuTheoThang();
}

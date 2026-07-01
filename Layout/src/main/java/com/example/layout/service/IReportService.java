package com.example.layout.service;

import com.example.layout.entity.ChuyenDuLich;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IReportService {
    Map<String, Long> thongKeNguonKhachHang();
    Map<String, BigDecimal> thongKeChiPhi(int year, int month);
    Map<String, BigDecimal> getTripFinanceReport(Integer maChuyen);
    List<Map<String, Object>> getLocationStatistics();
    List<Map<String, Object>> getTourPopularity();
    Map<String, BigDecimal> getDailyRevenueForMonth(int year, int month);
    List<ChuyenDuLich> getTripsForMonth(int year, int month);
    Map<String, Long> thongKeTrangThaiDatCho();
}

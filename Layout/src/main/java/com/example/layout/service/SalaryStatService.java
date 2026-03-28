package com.example.layout.service;

import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.Nhanvien;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.NhanvienRepository;
import com.example.layout.utils.TripStatusChecker;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service tính toán lương/thù lao (SRP).
 * Tách biệt khỏi IChuyenDuLichService để mỗi class chỉ có một lý do thay đổi.
 */
@Service
public class SalaryStatService implements ISalaryStatService {

    private final ChuyenDuLichRepository chuyenDuLichRepository;
    private final NhanvienRepository nhanVienRepository;

    public SalaryStatService(ChuyenDuLichRepository chuyenDuLichRepository,
                             NhanvienRepository nhanVienRepository) {
        this.chuyenDuLichRepository = chuyenDuLichRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    /**
     * Thống kê thù lao theo năm.
     * Tính: Lương cơ bản (12 tháng) + Tiền thuê các chuyến đi hoàn thành.
     */
    @Override
    public Map<String, Object> getYearlyStats(int year, Integer staffId) {
        Map<String, Object> result = new HashMap<>();

        Nhanvien nhanVien = nhanVienRepository.findById(staffId).orElse(null);
        BigDecimal luongCoBanThang = (nhanVien != null && nhanVien.getLuongCoBan() != null)
                ? nhanVien.getLuongCoBan() : BigDecimal.ZERO;

        List<Map<String, Object>> details = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        int totalTrips = 0;
        BigDecimal totalTripSalary = BigDecimal.ZERO;
        int totalHours = 0;
        double totalBonus = 0;

        for (int m = 1; m <= 12; m++) {
            List<ChuyenDuLich> trips = chuyenDuLichRepository.findByMonthYearAndStaff(m, year, staffId);

            int monthTrips = 0;
            BigDecimal monthTripSalary = BigDecimal.ZERO;
            int monthHours = 0;
            double monthBonus = 0;

            for (ChuyenDuLich t : trips) {
                if (TripStatusChecker.isCompleted(t.getTrangThai())) {
                    monthTrips++;
                    if (t.getNgayBatDau() != null && t.getNgayKetThuc() != null) {
                        long days = ChronoUnit.DAYS.between(t.getNgayBatDau(), t.getNgayKetThuc()) + 1;
                        monthHours += (int) (days * 24);
                    }
                    monthTripSalary = monthTripSalary.add(calculateTripSalary(t, staffId));
                    monthBonus += calculateBonus(t);
                }
            }

            BigDecimal monthTotal = luongCoBanThang.add(monthTripSalary).add(BigDecimal.valueOf(monthBonus));

            Map<String, Object> mdata = new HashMap<>();
            mdata.put("month", m);
            mdata.put("completedTrips", monthTrips);
            mdata.put("totalHours", monthHours);
            mdata.put("baseSalary", luongCoBanThang);
            mdata.put("tripSalary", monthTripSalary);
            mdata.put("bonus", monthBonus);
            mdata.put("totalSalary", monthTotal);
            details.add(mdata);

            labels.add("T" + m);
            values.add(monthTotal.doubleValue());

            totalTrips += monthTrips;
            totalTripSalary = totalTripSalary.add(monthTripSalary);
            totalHours += monthHours;
            totalBonus += monthBonus;
        }

        BigDecimal totalBaseSalary = luongCoBanThang.multiply(new BigDecimal(12));
        BigDecimal totalSalary = totalBaseSalary.add(totalTripSalary).add(BigDecimal.valueOf(totalBonus));

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("values", values);

        result.put("details", details);
        result.put("chartData", chartData);
        result.put("totalTrips", totalTrips);
        result.put("totalHours", totalHours);
        result.put("totalBaseSalary", totalBaseSalary);
        result.put("totalTripSalary", totalTripSalary);
        result.put("totalBonus", totalBonus);
        result.put("totalSalary", totalSalary);
        return result;
    }

    /**
     * Thống kê thù lao theo tháng.
     */
    @Override
    public Map<String, Object> getMonthlyStats(int year, int month, Integer staffId) {
        Map<String, Object> result = new HashMap<>();

        Nhanvien nhanVien = nhanVienRepository.findById(staffId).orElse(null);
        BigDecimal luongCoBan = (nhanVien != null && nhanVien.getLuongCoBan() != null)
                ? nhanVien.getLuongCoBan() : BigDecimal.ZERO;

        List<ChuyenDuLich> trips = chuyenDuLichRepository.findByMonthYearAndStaff(month, year, staffId);

        List<Map<String, Object>> details = new ArrayList<>();
        int totalTrips = 0;
        BigDecimal totalTripSalary = BigDecimal.ZERO;
        double totalBonus = 0;
        int totalHours = 0;

        for (ChuyenDuLich t : trips) {
            if (TripStatusChecker.isCompleted(t.getTrangThai())) {
                Map<String, Object> r = new HashMap<>();
                r.put("maChuyen", t.getMaChuyen());
                r.put("period", t.getNgayBatDau() + " → " + t.getNgayKetThuc());

                long days = (t.getNgayBatDau() != null && t.getNgayKetThuc() != null)
                        ? ChronoUnit.DAYS.between(t.getNgayBatDau(), t.getNgayKetThuc()) + 1 : 0;
                int hours = (int) (days * 24);
                r.put("hours", hours);

                BigDecimal tripSalary = calculateTripSalary(t, staffId);
                double bonus = calculateBonus(t);
                r.put("tripSalary", tripSalary);
                r.put("bonus", bonus);
                r.put("totalSalary", tripSalary.add(BigDecimal.valueOf(bonus)));

                details.add(r);
                totalTrips++;
                totalHours += hours;
                totalTripSalary = totalTripSalary.add(tripSalary);
                totalBonus += bonus;
            }
        }

        BigDecimal totalSalary = luongCoBan.add(totalTripSalary).add(BigDecimal.valueOf(totalBonus));

        result.put("details", details);
        result.put("totalTrips", totalTrips);
        result.put("totalHours", totalHours);
        result.put("totalBaseSalary", luongCoBan);
        result.put("totalTripSalary", totalTripSalary);
        result.put("totalBonus", totalBonus);
        result.put("totalSalary", totalSalary);

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", List.of("Lương Cơ Bản", "Tiền Thuê Chuyến", "Thưởng"));
        chartData.put("values", List.of(luongCoBan, totalTripSalary, totalBonus));
        result.put("chartData", chartData);
        return result;
    }

    /**
     * Thống kê thù lao theo khoảng thời gian tùy chọn.
     */
    @Override
    public Map<String, Object> getPeriodStats(LocalDate from, LocalDate to, Integer staffId) {
        Map<String, Object> result = new HashMap<>();

        List<ChuyenDuLich> trips = chuyenDuLichRepository.findByPeriodAndStaff(from, to, staffId);

        List<Map<String, Object>> details = new ArrayList<>();
        int totalTrips = 0;
        BigDecimal totalTripSalary = BigDecimal.ZERO;
        double totalBonus = 0;
        int totalHours = 0;

        for (ChuyenDuLich t : trips) {
            if (TripStatusChecker.isCompleted(t.getTrangThai())) {
                Map<String, Object> r = new HashMap<>();
                r.put("maChuyen", t.getMaChuyen());
                r.put("period", t.getNgayBatDau() + " → " + t.getNgayKetThuc());

                long days = (t.getNgayBatDau() != null && t.getNgayKetThuc() != null)
                        ? ChronoUnit.DAYS.between(t.getNgayBatDau(), t.getNgayKetThuc()) + 1 : 0;
                int hours = (int) (days * 24);
                r.put("hours", hours);

                BigDecimal tripSalary = calculateTripSalary(t, staffId);
                double bonus = calculateBonus(t);
                r.put("tripSalary", tripSalary);
                r.put("bonus", bonus);
                r.put("totalSalary", tripSalary.add(BigDecimal.valueOf(bonus)));

                details.add(r);
                totalTrips++;
                totalHours += hours;
                totalTripSalary = totalTripSalary.add(tripSalary);
                totalBonus += bonus;
            }
        }

        BigDecimal totalSalary = totalTripSalary.add(BigDecimal.valueOf(totalBonus));

        result.put("details", details);
        result.put("totalTrips", totalTrips);
        result.put("totalHours", totalHours);
        result.put("totalBaseSalary", BigDecimal.ZERO);
        result.put("totalTripSalary", totalTripSalary);
        result.put("totalBonus", totalBonus);
        result.put("totalSalary", totalSalary);

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", List.of("Tiền Thuê Chuyến", "Thưởng"));
        chartData.put("values", List.of(totalTripSalary, totalBonus));
        result.put("chartData", chartData);
        return result;
    }

    // === PRIVATE HELPERS ===

    /**
     * Tính tiền thuê chuyến cho một nhân viên cụ thể.
     */
    private BigDecimal calculateTripSalary(ChuyenDuLich t, Integer staffId) {
        BigDecimal salary = BigDecimal.ZERO;
        if (t.getHuongDanVien() != null
                && t.getHuongDanVien().getMaNhanVien().equals(staffId)
                && t.getGiaThueHDV() != null) {
            salary = salary.add(t.getGiaThueHDV());
        }
        if (t.getTaiXe() != null
                && t.getTaiXe().getMaNhanVien().equals(staffId)
                && t.getGiaThueTX() != null) {
            salary = salary.add(t.getGiaThueTX());
        }
        return salary;
    }

    /**
     * Tính thưởng cho chuyến đi (có thể mở rộng sau).
     */
    private double calculateBonus(ChuyenDuLich trip) {
        return 0;
    }
}

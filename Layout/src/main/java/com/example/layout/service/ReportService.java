package com.example.layout.service;

import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.LichTrinh;
import com.example.layout.repository.ChiTietDatChoRepository;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.DatChoRepository;
import com.example.layout.repository.KhachHangRepository;
import com.example.layout.repository.LichTrinhRepository;
import com.example.layout.repository.ThanhToanRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService implements IReportService {

    private final KhachHangRepository khachHangRepository;
    private final ChuyenDuLichRepository chuyenDuLichRepository;
    private final ChiTietDatChoRepository chiTietDatChoRepository;
    private final LichTrinhRepository lichTrinhRepository;
    private final DatChoRepository datChoRepository;
    private final ThanhToanRepository thanhToanRepository;

    public ReportService(KhachHangRepository khachHangRepository,
                         ChuyenDuLichRepository chuyenDuLichRepository,
                         ChiTietDatChoRepository chiTietDatChoRepository,
                         LichTrinhRepository lichTrinhRepository,
                         DatChoRepository datChoRepository,
                         ThanhToanRepository thanhToanRepository) {
        this.khachHangRepository = khachHangRepository;
        this.chuyenDuLichRepository = chuyenDuLichRepository;
        this.chiTietDatChoRepository = chiTietDatChoRepository;
        this.lichTrinhRepository = lichTrinhRepository;
        this.datChoRepository = datChoRepository;
        this.thanhToanRepository = thanhToanRepository;
    }

    private static final Set<String> VALID_SOURCES = new HashSet<>(Arrays.asList(
        "friend", "facebook", "tiktok", "google", "youtube", "website"
    ));

    @Override
    public Map<String, Long> thongKeNguonKhachHang() {
        Map<String, Long> result = new LinkedHashMap<>();
        List<Object[]> data = khachHangRepository.thongKeNguonKhachHang();

        VALID_SOURCES.forEach(source -> result.put(source, 0L));

        for (Object[] row : data) {
            String nguon = (String) row[0];
            Long soLuong = (Long) row[1];
            String normalizedSource = normalizeSource(nguon);
            result.merge(normalizedSource, soLuong, Long::sum);
        }

        return result;
    }

    private String normalizeSource(String source) {
        if (source == null) return "other";
        source = source.toLowerCase().trim();
        if (source.contains("bạn") || source.contains("người quen") ||
            source.contains("giới thiệu") || source.contains("friend")) return "friend";
        if (source.contains("facebook") || source.contains("fb")) return "facebook";
        if (source.contains("tiktok") || source.contains("douyin")) return "tiktok";
        if (source.contains("google") || source.contains("search") || source.contains("tìm kiếm")) return "google";
        if (source.contains("youtube") || source.contains("yt")) return "youtube";
        if (source.contains("web") || source.contains("blog")) return "website";
        return "other";
    }

    @Override
    public Map<String, BigDecimal> thongKeChiPhi(int year, int month) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        if (month == 0) month = LocalDate.now().getMonthValue();
        if (year == 0) year = LocalDate.now().getYear();

        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            result.put(LocalDate.of(year, month, day).toString(), BigDecimal.ZERO);
        }

        try {
            List<Object[]> expenses = thanhToanRepository.getExpensesByMonth(year, month);
            if (expenses != null) {
                for (Object[] row : expenses) {
                    if (row[0] != null && row[1] != null) {
                        LocalDate localDate = ((java.sql.Date) row[0]).toLocalDate();
                        BigDecimal amount = row[1] instanceof BigDecimal
                                ? (BigDecimal) row[1]
                                : new BigDecimal(row[1].toString());
                        result.put(localDate.toString(), amount);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy dữ liệu chi phí: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Map<String, BigDecimal> getTripFinanceReport(Integer maChuyen) {
        ChuyenDuLich chuyen = chuyenDuLichRepository.findById(maChuyen)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi với ID: " + maChuyen));

        BigDecimal totalRevenue = chiTietDatChoRepository.findTotalRevenueByChuyenId(maChuyen);
        BigDecimal guideCost = chuyen.getGiaThueHDV() != null ? chuyen.getGiaThueHDV() : BigDecimal.ZERO;
        BigDecimal driverCost = chuyen.getGiaThueTX() != null ? chuyen.getGiaThueTX() : BigDecimal.ZERO;

        List<LichTrinh> lichTrinhList = lichTrinhRepository.findByTour_MaTour(chuyen.getTour().getMaTour());
        BigDecimal accommodationCost = BigDecimal.ZERO;
        BigDecimal vehicleCost = BigDecimal.ZERO;

        for (LichTrinh item : lichTrinhList) {
            if (item.getKhachSan() != null && item.getKhachSan().getGiaTheoNgay() != null) {
                accommodationCost = accommodationCost.add(item.getKhachSan().getGiaTheoNgay());
            }
            if (item.getPhuongTien() != null && item.getPhuongTien().getGiaTheoNgay() != null) {
                vehicleCost = vehicleCost.add(item.getPhuongTien().getGiaTheoNgay());
            }
        }

        BigDecimal operationalCost = accommodationCost.add(vehicleCost);
        BigDecimal totalExpense = guideCost.add(driverCost).add(operationalCost);
        BigDecimal profit = totalRevenue.subtract(totalExpense);

        Map<String, BigDecimal> report = new HashMap<>();
        report.put("doanhThu", totalRevenue);
        report.put("chiPhiNhanSu", guideCost.add(driverCost));
        report.put("chiPhiVanHanh", operationalCost);
        report.put("tongChiPhi", totalExpense);
        report.put("loiNhuan", profit);
        return report;
    }

    @Override
    public List<Map<String, Object>> getLocationStatistics() {
        return lichTrinhRepository.findLocationUsageStatistics();
    }

    @Override
    public List<Map<String, Object>> getTourPopularity() {
        return datChoRepository.findTourPopularity();
    }

    @Override
    public Map<String, BigDecimal> getDailyRevenueForMonth(int year, int month) {
        List<Object[]> dailyData = thanhToanRepository.getExpensesByMonth(year, month);
        Map<String, BigDecimal> report = new LinkedHashMap<>();
        dailyData.forEach(row -> {
            String date = ((java.sql.Date) row[0]).toLocalDate().toString();
            BigDecimal revenue = (BigDecimal) row[1];
            report.put(date, revenue);
        });
        return report;
    }

    @Override
    public List<ChuyenDuLich> getTripsForMonth(int year, int month) {
        return chuyenDuLichRepository.findByYearAndMonth(year, month);
    }

    @Override
    public Map<String, Long> thongKeTrangThaiDatCho() {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("Chờ xác nhận", 0L);
        result.put("Đã xác nhận", 0L);
        result.put("Đã thanh toán", 0L);
        result.put("Hoàn thành", 0L);
        result.put("Đã hủy", 0L);

        try {
            List<Object[]> data = datChoRepository.countByTrangThaiGroupBy();
            for (Object[] row : data) {
                String trangThai = (String) row[0];
                Long soLuong = (Long) row[1];
                if (trangThai != null && soLuong != null) {
                    result.put(trangThai, soLuong);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy thống kê trạng thái đặt chỗ: " + e.getMessage());
        }

        return result;
    }
}

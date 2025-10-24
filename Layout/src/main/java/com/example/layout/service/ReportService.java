package com.example.layout.service;

import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.LichTrinh;
import com.example.layout.repository.ChiTietDatChoRepository;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.DatChoRepository;
import com.example.layout.repository.KhachHangRepository;
import com.example.layout.repository.LichTrinhRepository;
import com.example.layout.repository.ThanhToanRepository;
import com.example.layout.repository.DatChoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired private KhachHangRepository khachHangRepository;
    @Autowired private ChuyenDuLichRepository chuyenDuLichRepository;
    @Autowired private ChiTietDatChoRepository chiTietDatChoRepository;
    @Autowired private LichTrinhRepository lichTrinhRepository;
    @Autowired private DatChoRepository datChoRepository;
    @Autowired private ThanhToanRepository thanhToanRepository;


    private static final Set<String> VALID_SOURCES = new HashSet<>(Arrays.asList(
        "friend", "facebook", "tiktok", "google", "youtube", "website"
    ));

    public Map<String, Long> thongKeNguonKhachHang() {
        Map<String, Long> result = new LinkedHashMap<>();
        List<Object[]> data = khachHangRepository.thongKeNguonKhachHang();

        // Initialize all valid sources with 0
        VALID_SOURCES.forEach(source -> result.put(source, 0L));

        // Add counts from database
        for (Object[] row : data) {
            String nguon = (String) row[0];
            Long soLuong = (Long) row[1];

            // Normalize source name
            String normalizedSource = normalizeSource(nguon);
            result.merge(normalizedSource, soLuong, Long::sum);
        }

        return result;
    }

    private String normalizeSource(String source) {
        if (source == null) return "other";
        
        source = source.toLowerCase().trim();
        
        // Map common variations to standard sources
        if (source.contains("bạn") || source.contains("người quen") || 
            source.contains("giới thiệu") || source.contains("friend")) {
            return "friend";
        }
        if (source.contains("facebook") || source.contains("fb")) {
            return "facebook";
        }
        if (source.contains("tiktok") || source.contains("douyin")) {
            return "tiktok";
        }
        if (source.contains("google") || source.contains("search") || 
            source.contains("tìm kiếm")) {
            return "google";
        }
        if (source.contains("youtube") || source.contains("yt")) {
            return "youtube";
        }
        if (source.contains("web") || source.contains("blog")) {
            return "website";
        }
        
        return "other";
    }

    public Map<String, BigDecimal> thongKeChiPhi(int year, int month) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        
        // Lấy tháng hiện tại nếu không chỉ định
        if (month == 0) {
            month = LocalDate.now().getMonthValue();
        }
        if (year == 0) {
            year = LocalDate.now().getYear();
        }

        System.out.println("Đang tìm dữ liệu cho: Năm " + year + ", Tháng " + month);

        // Lấy số ngày trong tháng
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        
        // Tạo map cho tất cả các ngày trong tháng với giá trị mặc định là 0
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            result.put(date.toString(), BigDecimal.ZERO);
        }

        try {
            // Lấy dữ liệu chi phí thực tế từ database
            List<Object[]> expenses = thanhToanRepository.getExpensesByMonth(year, month);
            System.out.println("Số bản ghi tìm thấy: " + (expenses != null ? expenses.size() : 0));
            
            // Cập nhật kết quả với dữ liệu thực tế
            if (expenses != null) {
                for (Object[] row : expenses) {
                    if (row[0] != null && row[1] != null) {
                        // Xử lý ngày
                        java.sql.Date sqlDate = (java.sql.Date) row[0];
                        LocalDate localDate = sqlDate.toLocalDate();
                        String dateStr = localDate.toString();
                        
                        // Xử lý số tiền
                        BigDecimal amount;
                        if (row[1] instanceof BigDecimal) {
                            amount = (BigDecimal) row[1];
                        } else if (row[1] instanceof Number) {
                            amount = new BigDecimal(row[1].toString());
                        } else {
                            amount = new BigDecimal(row[1].toString());
                        }
                        
                        System.out.println("Dữ liệu tìm thấy: Ngày " + dateStr + ", Số tiền: " + amount);
                        result.put(dateStr, amount);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy dữ liệu chi phí: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
    public Map<String, BigDecimal> getTripFinanceReport(Integer maChuyen) {
        ChuyenDuLich chuyen = chuyenDuLichRepository.findById(maChuyen)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi với ID: " + maChuyen));

        // 1. TÍNH TỔNG THU (DOANH THU)
        BigDecimal totalRevenue = chiTietDatChoRepository.findTotalRevenueByChuyenId(maChuyen);

        // 2. TÍNH TỔNG CHI
        BigDecimal guideCost = chuyen.getGiaThueHDV() != null ? chuyen.getGiaThueHDV() : BigDecimal.ZERO;
        BigDecimal driverCost = chuyen.getGiaThueTX() != null ? chuyen.getGiaThueTX() : BigDecimal.ZERO;
        
        // Chi phí khách sạn, phương tiện (tính theo số ngày của tour)
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
        
        // Tổng chi phí vận hành
        BigDecimal operationalCost = accommodationCost.add(vehicleCost);
        BigDecimal totalExpense = guideCost.add(driverCost).add(operationalCost);
        
        // 3. TÍNH LỢI NHUẬN
        BigDecimal profit = totalRevenue.subtract(totalExpense);

        Map<String, BigDecimal> report = new HashMap<>();
        report.put("doanhThu", totalRevenue);
        report.put("chiPhiNhanSu", guideCost.add(driverCost)); // Chi phí nhân sự
        report.put("chiPhiVanHanh", operationalCost); // Chi phí vận hành
        report.put("tongChiPhi", totalExpense); // Tổng chi phí
        report.put("loiNhuan", profit);
        
        return report;
    }

    public List<Map<String, Object>> getLocationStatistics() {
        return lichTrinhRepository.findLocationUsageStatistics();
    }

    public List<Map<String, Object>> getTourPopularity() {
        return datChoRepository.findTourPopularity();
    }

    public Map<String, BigDecimal> getDailyRevenueForMonth(int year, int month) {
        // Tái sử dụng phương thức bạn đã có trong repository
        List<Object[]> dailyData = thanhToanRepository.getExpensesByMonth(year, month);
        
        Map<String, BigDecimal> report = new LinkedHashMap<>(); // Dùng LinkedHashMap để giữ thứ tự ngày
        dailyData.forEach(row -> {
            // Chuyển đổi java.sql.Date (hoặc kiểu dữ liệu tương ứng) sang String
            String date = ((java.sql.Date) row[0]).toLocalDate().toString();
            BigDecimal revenue = (BigDecimal) row[1];
            report.put(date, revenue);
        });
        return report;
    }

    public List<ChuyenDuLich> getTripsForMonth(int year, int month) {
        // Tái sử dụng phương thức bạn đã có trong repository
        return chuyenDuLichRepository.findByYearAndMonth(year, month);
    }
    public Map<String, Long> thongKeTrangThaiDatCho() {
        Map<String, Long> result = new LinkedHashMap<>();
        
        // Khởi tạo tất cả trạng thái với giá trị 0
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

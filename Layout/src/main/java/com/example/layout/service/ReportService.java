package com.example.layout.service;

import com.example.layout.repository.KhachHangRepository;
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

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private ThanhToanRepository thanhToanRepository;

    @Autowired
    private DatChoRepository datChoRepository;

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

package com.example.layout.controller.nhanvien;

import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.User;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.ThanhToanRepository;
import com.example.layout.service.ReportService;
import com.example.layout.service.ExportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.itextpdf.text.DocumentException;

@Controller
@RequestMapping("/nhanvien")
public class NhanvienReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ExportService exportService;

    @Autowired
    private ChuyenDuLichRepository chuyenDuLichRepository;

    @Autowired
    private ThanhToanRepository thanhToanRepository;



    @GetMapping("/report")
    public String showReportForm(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 2) {
            return "redirect:/access_denied";
        }
        return "nhanvien/report";
    }

    // ✅ API thống kê cho biểu đồ
    @GetMapping("/report/api/marketing")
    @ResponseBody
    public Map<String, Long> getMarketingReport(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 2) {
            return null;
        }
        return reportService.thongKeNguonKhachHang();
    }

    @GetMapping("/report/api/bookings")
    @ResponseBody
    public Map<String, Long> getBookingStatusReport(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 2) {
            return null;
        }
        return reportService.thongKeTrangThaiDatCho();
    }

    @GetMapping("/report/api/expense")
    @ResponseBody
    public Map<String, BigDecimal> getExpenseReport(
            @RequestParam(required = false, defaultValue = "0") int year,
            @RequestParam(required = false, defaultValue = "0") int month,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 2) {
            return null;
        }
        return reportService.thongKeChiPhi(year, month);
    }


    @GetMapping("/report/api/trip-finance/{maChuyen}")
    @ResponseBody
    public ResponseEntity<?> getTripFinance(@PathVariable Integer maChuyen, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 2) {
            return ResponseEntity.status(403).body("Không có quyền truy cập");
        }
        try {
            Map<String, BigDecimal> report = reportService.getTripFinanceReport(maChuyen);
            return ResponseEntity.ok(report);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * API: Thống kê các địa điểm phổ biến.
     */
    @GetMapping("/report/api/location-stats")
    @ResponseBody
    public ResponseEntity<?> getLocationStats(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 2) {
            return ResponseEntity.status(403).body("Không có quyền truy cập");
        }
        List<Map<String, Object>> stats = reportService.getLocationStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/report/api/tour-popularity")
    @ResponseBody
    public ResponseEntity<?> getTourPopularity(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 2) {
            return ResponseEntity.status(403).body("Không có quyền truy cập");
        }
        List<Map<String, Object>> stats = reportService.getTourPopularity();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/report/api/monthly-daily-revenue")
    @ResponseBody
    public ResponseEntity<?> getMonthlyDailyRevenue(@RequestParam int year, @RequestParam int month, HttpSession session) {
        // ... (code kiểm tra quyền của bạn)
        Map<String, BigDecimal> report = reportService.getDailyRevenueForMonth(year, month);
        return ResponseEntity.ok(report);
    }

    /**
     * API: Lấy danh sách các chuyến đi trong tháng để hiển thị bảng.
     */
    @GetMapping("/report/api/monthly-trip-list")
    @ResponseBody
    public ResponseEntity<?> getMonthlyTripList(@RequestParam int year, @RequestParam int month, HttpSession session) {
        // ... (code kiểm tra quyền của bạn)
        List<ChuyenDuLich> trips = reportService.getTripsForMonth(year, month);
        return ResponseEntity.ok(trips);
    }

    // ✅ Xuất báo cáo Excel
    @GetMapping("/report/export/excel")
    public ResponseEntity<byte[]> exportToExcel(
            @RequestParam String reportType,
            @RequestParam(required = false) String fromDate,
            HttpSession session) {
        
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 2) {
            return ResponseEntity.status(403).build();
        }
        
        try {
            LocalDate date = null;
            if (fromDate != null && !fromDate.isEmpty()) {
                // Parse năm-tháng format (2023-09)
                String[] parts = fromDate.split("-");
                if (parts.length == 2) {
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    date = LocalDate.of(year, month, 1); // Ngày đầu tiên của tháng
                }
            }
            
            byte[] excelFile = exportService.exportToExcel(reportType, date, null);
            
            String filename = String.format("bao-cao-%s-%s.xlsx", 
                reportType, 
                date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM")) : LocalDate.now());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelFile);
                    
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ Xuất báo cáo PDF
    @GetMapping("/report/export/pdf")
    public ResponseEntity<byte[]> exportToPDF(
            @RequestParam String reportType,
            @RequestParam(required = false) String fromDate,
            HttpSession session) {
        
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 2) {
            return ResponseEntity.status(403).build();
        }
        
        try {
            LocalDate date = null;
            if (fromDate != null && !fromDate.isEmpty()) {
                // Parse năm-tháng format (2023-09)
                String[] parts = fromDate.split("-");
                if (parts.length == 2) {
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    date = LocalDate.of(year, month, 1); // Ngày đầu tiên của tháng
                }
            }
            
            byte[] pdfFile = exportService.exportToPDF(reportType, date, null);
            
            String filename = String.format("bao-cao-%s-%s.pdf", 
                reportType, 
                date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM")) : LocalDate.now());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfFile);
                    
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
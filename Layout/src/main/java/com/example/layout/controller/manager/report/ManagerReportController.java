package com.example.layout.controller.manager.report;

import com.example.layout.entity.User;
import com.example.layout.service.ReportService;
import com.example.layout.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.util.Map;
import com.itextpdf.text.DocumentException;

@Controller
@RequestMapping("/manager")
public class ManagerReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ExportService exportService;

    @GetMapping("/report")
    public String showReportForm(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 1) {
            return "redirect:/access_denied";
        }
        return "manager/report";
    }

    // ✅ API thống kê cho biểu đồ
    @GetMapping("/report/api/marketing")
    @ResponseBody
    public Map<String, Long> getMarketingReport() {
        return reportService.thongKeNguonKhachHang();
    }

    @GetMapping("/report/api/bookings")
    @ResponseBody
    public Map<String, Long> getBookingStatusReport() {
        return reportService.thongKeTrangThaiDatCho();
    }

    @GetMapping("/report/api/expense")
    @ResponseBody
    public Map<String, BigDecimal> getExpenseReport(
            @RequestParam(required = false, defaultValue = "0") int year,
            @RequestParam(required = false, defaultValue = "0") int month) {
        return reportService.thongKeChiPhi(year, month);
    }

    // ✅ Xuất báo cáo Excel
    @GetMapping("/report/export/excel")
    public ResponseEntity<byte[]> exportToExcel(
            @RequestParam String reportType,
            @RequestParam(required = false) String fromDate) {
        
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
            @RequestParam(required = false) String fromDate) {
        
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

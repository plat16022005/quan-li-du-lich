package com.example.layout.controller.hdvtx.api;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.layout.entity.Nhanvien;
import com.example.layout.entity.User;
import com.example.layout.service.INhanVienService;
import com.example.layout.service.ISalaryStatService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/hdvtx/api/salary")
public class HdvTxSalaryApiController {

    private final INhanVienService nhanVienService;
    private final ISalaryStatService salaryStatService;

    public HdvTxSalaryApiController(INhanVienService nhanVienService,
                                    ISalaryStatService salaryStatService) {
        this.nhanVienService = nhanVienService;
        this.salaryStatService = salaryStatService;
    }

    @GetMapping("/year")
    public ResponseEntity<Map<String, Object>> getSalaryByYear(
            @RequestParam int year,
            HttpSession session) {
        
        // ✅ FIX: Lấy user từ session
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        
        Nhanvien nhanVien = nhanVienService.findByMaTaiKhoan(user.getMaTaiKhoan());
        if (nhanVien == null) {
            return ResponseEntity.status(404).build();
        }

        Map<String, Object> result = salaryStatService.getYearlyStats(year, nhanVien.getMaNhanVien());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/month")
    public ResponseEntity<Map<String, Object>> getSalaryByMonth(
            @RequestParam int year,
            @RequestParam int month,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        
        Nhanvien nhanVien = nhanVienService.findByMaTaiKhoan(user.getMaTaiKhoan());
        if (nhanVien == null) {
            return ResponseEntity.status(404).build();
        }

        Map<String, Object> result = salaryStatService.getMonthlyStats(year, month, nhanVien.getMaNhanVien());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/period")
    public ResponseEntity<Map<String, Object>> getSalaryByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        
        Nhanvien nhanVien = nhanVienService.findByMaTaiKhoan(user.getMaTaiKhoan());
        if (nhanVien == null) {
            return ResponseEntity.status(404).build();
        }

        Map<String, Object> result = salaryStatService.getPeriodStats(from, to, nhanVien.getMaNhanVien());
        return ResponseEntity.ok(result);
    }
}
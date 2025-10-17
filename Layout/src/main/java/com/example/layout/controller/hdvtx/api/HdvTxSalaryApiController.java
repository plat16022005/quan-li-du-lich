package com.example.layout.controller.hdvtx.api;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.layout.entity.Nhanvien;
import com.example.layout.entity.User;
import com.example.layout.service.ChuyenDuLichService;
import com.example.layout.service.NhanVienService;

@RestController
@RequestMapping("/hdvtx/api/salary")
public class HdvTxSalaryApiController {

    @Autowired
    private NhanVienService nhanVienService;

    @Autowired
    private ChuyenDuLichService chuyenDuLichService;


    @GetMapping("/year")
    public ResponseEntity<Map<String, Object>> getSalaryByYear(
            @RequestParam int year,
            Principal principal) {

        User user = nhanVienService.findTaiKhoanByUsername(principal.getName());
        Nhanvien nhanVien = nhanVienService.findByMaTaiKhoan(user.getMaTaiKhoan());

        Map<String, Object> result = chuyenDuLichService.getYearlyStats(year, nhanVien.getMaNhanVien());
        return ResponseEntity.ok(result);
    }

    // Thống kê theo tháng cụ thể (trả về chi tiết ngày trong tháng)
    @GetMapping("/month")
    public ResponseEntity<Map<String, Object>> getSalaryByMonth(
            @RequestParam int year,
            @RequestParam int month,
            Principal principal) {

        User user = nhanVienService.findTaiKhoanByUsername(principal.getName());
        Nhanvien nhanVien = nhanVienService.findByMaTaiKhoan(user.getMaTaiKhoan());

        Map<String, Object> result = chuyenDuLichService.getMonthlyStats(year, month, nhanVien.getMaNhanVien());
        return ResponseEntity.ok(result);
    }

    // Thống kê theo khoảng thời gian (từ - đến)
    @GetMapping("/period")
    public ResponseEntity<Map<String, Object>> getSalaryByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Principal principal) {

        User user = nhanVienService.findTaiKhoanByUsername(principal.getName());
        Nhanvien nhanVien = nhanVienService.findByMaTaiKhoan(user.getMaTaiKhoan());

        Map<String, Object> result = chuyenDuLichService.getPeriodStats(from, to, nhanVien.getMaNhanVien());
        return ResponseEntity.ok(result);
    }
}
package com.example.layout.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.layout.service.NhanVienDashboardService;

import java.util.Map;
import java.util.List;

@Controller
@RequestMapping("/nhanvien")
public class NhanVienDashboardController {

    @Autowired
    private NhanVienDashboardService dashboardService;

    @GetMapping("/dashboard")
    public String showDashboard(jakarta.servlet.http.HttpSession session) {
        com.example.layout.entity.User user = (com.example.layout.entity.User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 2) {
            return "redirect:/access_denied";
        }
        return "nhanvien/dashboard";
    }

    @GetMapping("/dashboard/api/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/dashboard/api/revenue-week")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getWeekRevenue() {
        Map<String, Object> revenue = dashboardService.getWeekRevenue();
        return ResponseEntity.ok(revenue);
    }
}
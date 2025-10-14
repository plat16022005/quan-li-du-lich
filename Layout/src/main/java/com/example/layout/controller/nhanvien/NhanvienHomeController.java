package com.example.layout.controller.nhanvien;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.layout.entity.User;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.DatChoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/nhanvien")
public class NhanvienHomeController {

    @Autowired
    private DatChoRepository datChoRepository;
    @Autowired
    private ChuyenDuLichRepository chuyenDuLichRepository;


    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 2) {
            return "redirect:/access-denied";
        }
        return "nhanvien/dashboard"; // View: /templates/nhanvien/dashboard.html
    }

    /**
     * API: Cung cấp các số liệu thống kê cho dashboard.
     */
    @GetMapping("/dashboard/api/stats")
    @ResponseBody
    public ResponseEntity<?> getDashboardStats(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 2) {
            // Nếu không phải nhân viên, trả về lỗi "Forbidden" (Cấm truy cập)
            return ResponseEntity.status(403).body("Bạn không có quyền truy cập tài nguyên này.");
        }
        long pendingBookings = datChoRepository.countByTrangThai("Chờ xác nhận");
        long upcomingTrips = chuyenDuLichRepository.countByTrangThai("Sắp diễn ra");
        // Bạn có thể thêm các số liệu khác ở đây

        Map<String, Long> stats = new HashMap<>();
        stats.put("pendingBookings", pendingBookings);
        stats.put("upcomingTrips", upcomingTrips);
        
        return ResponseEntity.ok(stats);
    }
}

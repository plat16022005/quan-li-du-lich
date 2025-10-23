package com.example.layout.controller.manager.home;

import jakarta.servlet.http.HttpSession;

import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.User;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.service.HomeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/manager")
public class ManagerHomeController {
    @Autowired
    private ChuyenDuLichRepository chuyenDuLichRepository;
	@GetMapping("/home")
    public String showHomeForm(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null)
		{
			return "redirect:/access_denied";
		}
		if (user.getMaVaiTro() != 1)
		{
			return "redirect:/access_denied";
		}
        return "manager/home";
    }
    @Autowired
    private HomeService dashboardService;

    @GetMapping("/home/stats")
    @ResponseBody
    public Map<String, Object> getDashboardStats(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 1) {
            throw new RuntimeException("Không có quyền truy cập");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("chuyenDangDienRa", dashboardService.getSoChuyenDangDienRa());
        response.put("khachHangMoi", dashboardService.getSoKhachHangMoi());
        response.put("tongNhanVien", dashboardService.getSoNhanVien());
        // totalRevenue in VND (BigDecimal) -> return as number
        java.math.BigDecimal revenue = dashboardService.getDoanhThuThang();
        response.put("totalRevenue", revenue != null ? revenue : java.math.BigDecimal.ZERO);
        return response;
    }
    @GetMapping("/home/upcoming-tours")
    @ResponseBody
    public List<ChuyenDuLich> getUpcomingTours() {
        return chuyenDuLichRepository.findByTrangThai("Sắp diễn ra");
    }
    
    @GetMapping("/home/revenue-chart")
    @ResponseBody
    public Map<String, Object> getRevenueChart(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 1) {
            throw new RuntimeException("Không có quyền truy cập");
        }
        return dashboardService.getDoanhThuTheoThang();
    }
}

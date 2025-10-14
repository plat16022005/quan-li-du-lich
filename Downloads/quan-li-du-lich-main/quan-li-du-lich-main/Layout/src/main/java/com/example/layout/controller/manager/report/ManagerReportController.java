package com.example.layout.controller.manager.report;

import com.example.layout.entity.User;
import com.example.layout.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
import java.util.Map;

@Controller
@RequestMapping("/manager")
public class ManagerReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/report")
    public String showReportForm(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 1) {
            return "redirect:/access-denied";
        }
        return "manager/report";
    }

    // ✅ API thống kê cho biểu đồ
    @GetMapping("/report/api/marketing")
    @ResponseBody
    public Map<String, Long> getMarketingReport() {
        return reportService.thongKeNguonKhachHang();
    }
}

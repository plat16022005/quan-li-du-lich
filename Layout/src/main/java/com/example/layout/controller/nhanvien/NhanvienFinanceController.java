package com.example.layout.controller.nhanvien;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.layout.dto.FinanceReportDTO;
import com.example.layout.dto.TripFinanceSummaryDTO;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.User;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.DatChoRepository;
import com.example.layout.service.FinanceService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/nhanvien")
public class NhanvienFinanceController {
	@Autowired
	private ChuyenDuLichRepository chuyenDuLichRepository;
	@Autowired
	private DatChoRepository datChoRepository;
	@Autowired
	private FinanceService financeService;
    @GetMapping("/finance")
    public String showManagerTour(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 2) {
            return "redirect:/access_denied";
        }
        List<ChuyenDuLich> completedTrips = chuyenDuLichRepository.findAllCompletedTrips();
        model.addAttribute("completedTrips", completedTrips);        
        return "nhanvien/finance";
    }
    @GetMapping("/finance/{maChuyen}")
    public String showFinanceDetail(@PathVariable("maChuyen") Integer maChuyen,
                                    HttpSession session,
                                    Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 2) {
            return "redirect:/access_denied";
        }

        // ✅ Lấy thông tin chuyến
        ChuyenDuLich trip = chuyenDuLichRepository.findById(maChuyen)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến có mã " + maChuyen));

        // ✅ Lấy báo cáo tài chính cho chuyến
        FinanceReportDTO report = financeService.getFinanceReport(maChuyen);
        Long soVe = datChoRepository.getSoldTicketCount(maChuyen);

        // ✅ Đưa dữ liệu ra view
        model.addAttribute("trip", trip);
        model.addAttribute("report", report);
        model.addAttribute("soldTickets", soVe);

        return "nhanvien/finance_detail"; 
    }


}

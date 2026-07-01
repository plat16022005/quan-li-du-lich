package com.example.layout.controller.nhanvien;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.layout.dto.FinanceReportDTO;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.User;
import com.example.layout.service.IChuyenDuLichService;
import com.example.layout.service.IDatChoService;
import com.example.layout.service.IFinanceService;
import com.example.layout.utils.VaiTroConstants;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/nhanvien")
public class NhanvienFinanceController {
	private final IChuyenDuLichService chuyenDuLichService;
	private final IDatChoService datChoService;
	private final IFinanceService financeService;

    public NhanvienFinanceController(IChuyenDuLichService chuyenDuLichService, IDatChoService datChoService, IFinanceService financeService) {
        this.chuyenDuLichService = chuyenDuLichService;
        this.datChoService = datChoService;
        this.financeService = financeService;
    }

    @GetMapping("/finance")
    public String showManagerTour(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != VaiTroConstants.QUAN_LY_TOUR) {
            return "redirect:/access_denied";
        }
        List<ChuyenDuLich> completedTrips = chuyenDuLichService.findAllCompletedTrips();
        model.addAttribute("completedTrips", completedTrips);        
        return "nhanvien/finance";
    }
    @GetMapping("/finance/{maChuyen}")
    public String showFinanceDetail(@PathVariable("maChuyen") Integer maChuyen,
                                    HttpSession session,
                                    Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != VaiTroConstants.QUAN_LY_TOUR) {
            return "redirect:/access_denied";
        }

        // ✅ Lấy thông tin chuyến
        ChuyenDuLich trip = Optional.ofNullable(chuyenDuLichService.getChuyenById(maChuyen))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến có mã " + maChuyen));

        // ✅ Lấy báo cáo tài chính cho chuyến
        FinanceReportDTO report = financeService.getFinanceReport(maChuyen);
        Long soVe = datChoService.getSoldTicketCount(maChuyen);

        // ✅ Đưa dữ liệu ra view
        model.addAttribute("trip", trip);
        model.addAttribute("report", report);
        model.addAttribute("soldTickets", soVe);

        return "nhanvien/finance_detail"; 
    }


}

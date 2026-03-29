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
import com.example.layout.utils.VaiTroConstants;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/nhanvien")
public class NhanvienHomeController {

    public NhanvienHomeController() {
    }



    // Dashboard được xử lý bởi NhanVienDashboardController

    @GetMapping("/manager_tour")
    public String showManagerTour(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != VaiTroConstants.QUAN_LY_TOUR) {
            return "redirect:/access_denied";
        }
        return "nhanvien/manager_tour";
    }

    @GetMapping("/add_tour")
    public String showAddTour(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != VaiTroConstants.QUAN_LY_TOUR) {
            return "redirect:/access_denied";
        }
        return "nhanvien/add_tour";
    }
}

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


    // Dashboard được xử lý bởi NhanVienDashboardController

    @GetMapping("/manager_tour")
    public String showManagerTour(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 2) {
            return "redirect:/access_denied";
        }
        return "nhanvien/manager_tour";
    }

    @GetMapping("/add_tour")
    public String showAddTour(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 2) {
            return "redirect:/access_denied";
        }
        return "nhanvien/add_tour";
    }
}

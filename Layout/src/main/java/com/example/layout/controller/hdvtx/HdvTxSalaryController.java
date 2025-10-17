package com.example.layout.controller.hdvtx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.layout.entity.Nhanvien;
import com.example.layout.entity.User;
import com.example.layout.service.NhanVienService;

import java.security.Principal;

@Controller
@RequestMapping("/hdvtx")
public class HdvTxSalaryController {

    @Autowired
    private NhanVienService nhanVienService;

    @GetMapping("/thong-ke-thu-lao")
    public String viewSalaryStats(Model model, Principal principal) {
        // Lấy thông tin tài khoản hiện tại và nhân viên tương ứng
        if (principal == null) {
            return "redirect:/login";
        }
        User currentUser = nhanVienService.findTaiKhoanByUsername(principal.getName());
        Nhanvien nhanVien = nhanVienService.findByMaTaiKhoan(currentUser.getMaTaiKhoan());

        model.addAttribute("nhanVien", nhanVien);
        // Nếu bạn muốn hiển thị id trên client để debug (không cần thiết), có thể thêm:
        model.addAttribute("currentStaffId", nhanVien != null ? nhanVien.getMaNhanVien() : null);

        return "hdvtx/thong-ke-thu-lao";
    }
}
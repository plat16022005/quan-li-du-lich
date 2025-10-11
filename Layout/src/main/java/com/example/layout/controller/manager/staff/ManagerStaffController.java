package com.example.layout.controller.manager.staff;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.layout.entity.Nhanvien;
import com.example.layout.entity.User;
import com.example.layout.repository.NhanvienRepository;
import com.example.layout.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/manager")
public class ManagerStaffController {

    @Autowired
    private NhanvienRepository nhanvienRepository;
    
    @Autowired
    private UserRepository userRepository;
    // ✅ Vào trang quản lý nhân viên
    @GetMapping("/staff")
    public String showStaffForm(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/access-denied";
        }
        if (user.getMaVaiTro() != 1) {
            return "redirect:/access-denied";
        }
        return "manager/staff";
    }

    // ✅ Thêm nhân viên
    @PostMapping("/staff/add")
    public String addStaff(
            @RequestParam("hoTen") String hoTen,
            @RequestParam("email") String email,
            @RequestParam("matKhau") String matKhau,
            @RequestParam("maVaiTro") int maVaiTro,
            @RequestParam("ngayVaoLam") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayVaoLam,
            @RequestParam("luongCoBan") double luongCoBan,
            HttpSession session) {

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || currentUser.getMaVaiTro() != 1) {
            return "redirect:/access-denied";
        }

        // ✅ 1. Tạo tài khoản User
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setMatKhau(matKhau);  // Nên hash mật khẩu trong thực tế
        newUser.setMaVaiTro(maVaiTro);
        newUser.setTrangThai(true);
        userRepository.save(newUser);

        // ✅ 2. Tạo NhanVien và liên kết với User
        Nhanvien nv = new Nhanvien();
        nv.setHoTen(hoTen);
        nv.setEmail(email);
        nv.setNgayVaoLam(ngayVaoLam);
        nv.setLuongCoBan(luongCoBan);
        nv.setTrangThai(true);
        nv.setUser(newUser); // nếu có quan hệ mapped

        nhanvienRepository.save(nv);

        return "redirect:/manager/staff";
    }

}

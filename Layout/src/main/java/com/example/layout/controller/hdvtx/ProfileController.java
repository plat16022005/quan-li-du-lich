// Layout/src/main/java/com/example/layout/controller/hdvtx/ProfileController.java
package com.example.layout.controller.hdvtx;

import com.example.layout.entity.Nhanvien;
import com.example.layout.entity.User;
import com.example.layout.service.INhanVienService;
import com.example.layout.service.IUserService;
import com.example.layout.controller.hdvtx.HdvTxDashboardController.CurrentUserDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/hdvtx/profile")
public class ProfileController {

    private final INhanVienService nhanVienService;
    private final IUserService userService;

    public ProfileController(INhanVienService nhanVienService, IUserService userService) {
        this.nhanVienService = nhanVienService;
        this.userService = userService;
    }


    /**
     * Hiển thị trang thông tin cá nhân
     */
    @GetMapping
    public String showProfile(Model model, HttpSession session, HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Nhanvien nhanvien = nhanVienService.findByMaTaiKhoan(user.getMaTaiKhoan());

        if (nhanvien == null) {
            return "redirect:/hdvtx/dashboard";
        }

        CurrentUserDTO currentUser = new CurrentUserDTO(
            user.getHoTen(),
            nhanvien.getChucVu(),
            user.getMaVaiTro(),
            nhanvien.getMaNhanVien()
        );

        model.addAttribute("user", user);
        model.addAttribute("nhanvien", nhanvien);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUri", request.getRequestURI());

        return "hdvtx/profile";
    }

    /**
     * Cập nhật thông tin cá nhân
     */
    @PostMapping("/update")
    public String updateProfile(
            @RequestParam String hoTen,
            @RequestParam String email,
            @RequestParam String soDienThoai,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            userService.updateProfile(user.getMaTaiKhoan(), hoTen, email, soDienThoai);
            User existingUser = userService.findById(user.getMaTaiKhoan());
            session.setAttribute("user", existingUser);

            redirectAttributes.addFlashAttribute("successMessage", 
                "✅ Cập nhật thông tin thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "❌ Lỗi: " + e.getMessage());
        }

        return "redirect:/hdvtx/profile";
    }

    /**
     * Đổi mật khẩu
     */
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "❌ Mật khẩu mới và xác nhận không khớp!");
                return "redirect:/hdvtx/profile?tab=password";
            }

            userService.changePassword(user.getMaTaiKhoan(), currentPassword, newPassword);
            User existingUser = userService.findById(user.getMaTaiKhoan());
            session.setAttribute("user", existingUser);

            redirectAttributes.addFlashAttribute("successMessage", 
                "✅ Đổi mật khẩu thành công!");
            
            return "redirect:/hdvtx/profile?tab=password";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "❌ Lỗi: " + e.getMessage());
            return "redirect:/hdvtx/profile?tab=password";
        }
    }
}
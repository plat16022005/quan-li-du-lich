package com.example.layout.controller.manager.staff;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.layout.entity.Nhanvien;
import com.example.layout.entity.User;
import com.example.layout.service.INhanVienService;
import com.example.layout.utils.VaiTroConstants;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/manager")
public class ManagerStaffController {

    private final INhanVienService nhanVienService;

    public ManagerStaffController(INhanVienService nhanVienService) {
        this.nhanVienService = nhanVienService;
    }

    
    // ✅ Vào trang quản lý nhân viên
    @GetMapping("/staff")
    public String showStaffForm(
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) Boolean status
    ) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != VaiTroConstants.ADMIN) {
            return "redirect:/access_denied";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Nhanvien> staffPage = nhanVienService.searchStaff(
                (keyword == null || keyword.isBlank()) ? null : keyword,
                role,
                status,
                pageable
        );

        model.addAttribute("staffList", staffPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", staffPage.getTotalPages());
        model.addAttribute("hasNext", staffPage.hasNext());
        model.addAttribute("hasPrev", staffPage.hasPrevious());

        model.addAttribute("keyword", keyword);
        model.addAttribute("role", role);
        model.addAttribute("status", status);

        return "manager/staff";
    }
    // ✅ Thêm nhân viên
    @PostMapping("/staff/add")
    public String addStaff(
        @RequestParam("hoTen") String hoTen,
        @RequestParam("tenDangNhap") String tenDangNhap,
        @RequestParam("matKhau") String matKhau,
        @RequestParam("email") String email,
        @RequestParam("maVaiTro") Integer maVaiTro,
        @RequestParam("soDienThoai") String soDienThoai,
        @RequestParam("ngayVaoLam") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayVaoLam
    ) {
        try {
            nhanVienService.addStaff(hoTen, tenDangNhap, matKhau, email, maVaiTro, soDienThoai, ngayVaoLam);
            return "redirect:/manager/staff?success=true";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @PostMapping("/staff/salary")
    @ResponseBody
    public String saveSalary(
                                @RequestParam("maNhanVien") Integer maNhanVien,
                                @RequestParam("luongCoBan") BigDecimal luongCoBan,
                                @RequestParam("soNgayLam") Integer soNgayLam,
                                @RequestParam("phuCap") BigDecimal phuCap
                                ) 
    {
        try {
            nhanVienService.saveSalary(maNhanVien, luongCoBan, soNgayLam, phuCap);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }
    @GetMapping("/staff/salary/{id}")
    @ResponseBody
    public BigDecimal getCurrentSalary(@PathVariable("id") Integer maNhanVien) {
        return nhanVienService.getCurrentSalary(maNhanVien);
    }
    @PostMapping("/staff/delete/{id}")
    @ResponseBody
    public String deleteStaff(@PathVariable("id") Integer maTaiKhoan) {
        try {
            nhanVienService.deleteStaff(maTaiKhoan);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }
    @PostMapping("/staff/update/{id}")
    @ResponseBody
    public String updateStaff(
            @PathVariable("id") Integer maNhanVien,
            @RequestParam("hoTen") String hoTen,
            @RequestParam("email") String email,
            @RequestParam("soDienThoai") String soDienThoai,
            @RequestParam("chucVu") String chucVu,
            @RequestParam("ngayVaoLam") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayVaoLam
    ) {
        try {
            nhanVienService.updateStaff(maNhanVien, hoTen, email, soDienThoai, chucVu, ngayVaoLam);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }

}

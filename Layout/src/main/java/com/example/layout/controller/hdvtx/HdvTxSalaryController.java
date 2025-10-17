package com.example.layout.controller.hdvtx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.layout.entity.Nhanvien;
import com.example.layout.entity.User;
import com.example.layout.service.NhanVienService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/hdvtx")
public class HdvTxSalaryController {

    @Autowired
    private NhanVienService nhanVienService;

    @GetMapping("/thong-ke-thu-lao")
    public String viewSalaryStats(Model model, HttpSession session) {
        // ✅ FIX: Lấy user từ session thay vì Principal
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // Kiểm tra vai trò (chỉ HDV và Tài xế mới được xem)
        if (currentUser.getMaVaiTro() != 3 && currentUser.getMaVaiTro() != 5) {
            return "redirect:/hdvtx/dashboard";
        }
        
        Nhanvien nhanVien = nhanVienService.findByMaTaiKhoan(currentUser.getMaTaiKhoan());
        
        if (nhanVien == null) {
            return "redirect:/hdvtx/dashboard";
        }

        model.addAttribute("nhanVien", nhanVien);
        model.addAttribute("currentStaffId", nhanVien.getMaNhanVien());
        model.addAttribute("currentUser", new CurrentUserDTO(
            currentUser.getHoTen(),
            nhanVien.getChucVu(),
            currentUser.getMaVaiTro(),
            nhanVien.getMaNhanVien()
        ));

        return "hdvtx/thong-ke-thu-lao";
    }
    
    // DTO helper class
    public static class CurrentUserDTO {
        private final String hoTen;
        private final String chucVu;
        private final int maVaiTro;
        private final int maNhanVien;

        public CurrentUserDTO(String hoTen, String chucVu, int maVaiTro, int maNhanVien) {
            this.hoTen = hoTen;
            this.chucVu = chucVu;
            this.maVaiTro = maVaiTro;
            this.maNhanVien = maNhanVien;
        }

        public String getHoTen() { return hoTen; }
        public String getChucVu() { return chucVu; }
        public int getMaVaiTro() { return maVaiTro; }
        public int getMaNhanVien() { return maNhanVien; }
    }
}
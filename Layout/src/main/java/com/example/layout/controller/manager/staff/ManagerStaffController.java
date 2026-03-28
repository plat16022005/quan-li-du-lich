package com.example.layout.controller.manager.staff;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.layout.entity.Nhanvien;
import com.example.layout.entity.User;
import com.example.layout.entity.Vaitro;
import com.example.layout.repository.NhanvienRepository;
import com.example.layout.repository.UserRepository;
import com.example.layout.repository.VaiTroRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/manager")
public class ManagerStaffController {

    private final NhanvienRepository nhanvienRepository;
    
    private final UserRepository userRepository;
    
    private final UserRepository taiKhoanRepository;
    
    private final VaiTroRepository vaiTroRepository;

    public ManagerStaffController(NhanvienRepository nhanvienRepository, UserRepository userRepository, UserRepository taiKhoanRepository, VaiTroRepository vaiTroRepository) {
        this.nhanvienRepository = nhanvienRepository;
        this.userRepository = userRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.vaiTroRepository = vaiTroRepository;
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
        if (user == null || user.getMaVaiTro() != 1) {
            return "redirect:/access_denied";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Nhanvien> staffPage = nhanvienRepository.searchStaff(
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
        // ⚠️ Kiểm tra tài khoản đã tồn tại
        if (taiKhoanRepository.existsByTenDangNhap(tenDangNhap)) {
            throw new RuntimeException("Tên đăng nhập " + tenDangNhap + " đã tồn tại!");    
        }
        if (taiKhoanRepository.existsByEmail(email)) {
            // có thể set flash attribute thông báo lỗi
            throw new RuntimeException("Email " + email + " đã tồn tại!");
        }
    

        // ✅ Tạo tài khoản
        User taiKhoan = new User();
        taiKhoan.setTenDangNhap(tenDangNhap);
        taiKhoan.setMatKhau(matKhau); // Thực tế nên mã hóa password (BCrypt)
        taiKhoan.setHoTen(hoTen);
        taiKhoan.setEmail(email);
        taiKhoan.setSoDienThoai(soDienThoai);
        taiKhoan.setTrangThai(true);

        Vaitro vaiTro = vaiTroRepository.findById(maVaiTro).orElse(null);
        taiKhoan.setMaVaiTro(maVaiTro);

        taiKhoanRepository.save(taiKhoan);

        // ✅ Tạo nhân viên
        Nhanvien nv = new Nhanvien();
        nv.setTaiKhoan(taiKhoan);
        if (maVaiTro == 2)
        {
            nv.setChucVu("Điều hành Tour");
        }
        else if (maVaiTro == 3)
        {
            nv.setChucVu("Hướng dẫn viên");
        }
        else if (maVaiTro == 5)
        {
            nv.setChucVu("Tài xế");
        }
        nv.setNgayVaoLam(ngayVaoLam);

        nhanvienRepository.save(nv);

        return "redirect:/manager/staff?success=true";
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
        System.out.println("ID nhận được từ front-end: " + maNhanVien);
        Nhanvien nv = nhanvienRepository.findById(maNhanVien).orElse(null);
        
        if (nv == null) 
        {
            System.out.println("Lỗi nè");
            return "error";
        }

        // 💰 Tính lương cơ bản mới
        BigDecimal tongLuong = luongCoBan
            .add(new BigDecimal(soNgayLam).multiply(new BigDecimal(200000)))
            .add(phuCap);

        // ✅ Cập nhật lương cơ bản
        nv.setLuongCoBan(tongLuong);
        nhanvienRepository.save(nv);

        return "success";
    }
    @GetMapping("/staff/salary/{id}")
    @ResponseBody
    public BigDecimal getCurrentSalary(@PathVariable("id") Integer maNhanVien) {
        Nhanvien nv = nhanvienRepository.findById(maNhanVien).orElse(null);
        if (nv == null || nv.getLuongCoBan() == null) {
            return BigDecimal.ZERO; // nếu chưa có lương thì trả về 0
        }
        return nv.getLuongCoBan();
    }
    @PostMapping("/staff/delete/{id}")
    @ResponseBody
    public String deleteStaff(@PathVariable("id") Integer maTaiKhoan) {
        User taiKhoan = userRepository.findById(maTaiKhoan).orElse(null);
        if (taiKhoan == null) {
            return "error";
        }

        taiKhoan.setTrangThai(!taiKhoan.getTrangThai());  // 🔥 khóa tài khoản
        userRepository.save(taiKhoan);

        return "success";
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
        Nhanvien nv = nhanvienRepository.findById(maNhanVien).orElse(null);
        if (nv == null) return "error";

        // cập nhật thông tin tài khoản
        User tk = nv.getTaiKhoan();
        tk.setHoTen(hoTen);
        tk.setEmail(email);
        tk.setSoDienThoai(soDienThoai);
        taiKhoanRepository.save(tk);

        // cập nhật thông tin nhân viên
        nv.setChucVu(chucVu);
        nv.setNgayVaoLam(ngayVaoLam);
        nhanvienRepository.save(nv);

        return "success";
    }

}

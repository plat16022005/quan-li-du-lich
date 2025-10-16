package com.example.layout.controller.hdvtx;

import com.example.layout.dto.HanhKhachDTO;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.LichTrinh;
import com.example.layout.entity.Nhanvien;
import com.example.layout.entity.User;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.KhachHangRepository;
import com.example.layout.repository.NhanvienRepository;
import com.example.layout.service.HdvTxService;
import com.example.layout.service.LichTrinhService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/hdvtx")
public class HdvTxDashboardController {

    @Autowired private HdvTxService hdvTxService;
    @Autowired private NhanvienRepository nhanvienRepository;
    @Autowired private ChuyenDuLichRepository chuyenDuLichRepository;
    @Autowired private KhachHangRepository khachHangRepository;
    @Autowired private LichTrinhService lichTrinhService;

    // Lớp DTO không thay đổi
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

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session, HttpServletRequest request) {
        CurrentUserDTO currentUser = getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<ChuyenDuLich> availableTrips = hdvTxService.getAvailableTrips(currentUser);
        List<ChuyenDuLich> assignedTrips = hdvTxService.getAssignedTrips(currentUser);

        model.addAttribute("availableTrips", availableTrips);
        model.addAttribute("assignedTrips", assignedTrips);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUri", request.getRequestURI());

        return "hdvtx/dashboard";
    }

    @GetMapping("/assigned-trips")
    public String showAssignedTrips(Model model, HttpSession session, HttpServletRequest request) {
        CurrentUserDTO currentUser = getCurrentUser(session);
        if (currentUser == null) return "redirect:/login";

        List<ChuyenDuLich> assignedTrips = hdvTxService.getAssignedTrips(currentUser);
        assignedTrips.forEach(chuyen ->
            chuyen.setSoLuongHienTai(chuyenDuLichRepository.getTotalParticipants(chuyen.getMaChuyen()))
        );

        model.addAttribute("assignedTrips", assignedTrips);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUri", request.getRequestURI());
        return "hdvtx/assigned-trips";
    }

    @GetMapping("/available-trips")
    public String showAvailableTrips(Model model, HttpSession session, HttpServletRequest request) {
        CurrentUserDTO currentUser = getCurrentUser(session);
        if (currentUser == null) return "redirect:/login";

        List<ChuyenDuLich> availableTrips = hdvTxService.getAvailableTrips(currentUser);
        availableTrips.forEach(chuyen ->
            chuyen.setSoLuongHienTai(chuyenDuLichRepository.getTotalParticipants(chuyen.getMaChuyen()))
        );

        model.addAttribute("availableTrips", availableTrips);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUri", request.getRequestURI());
        return "hdvtx/available-trips";
    }

 // ... (các import và các phương thức khác giữ nguyên)

    @GetMapping("/trip-details/{id}")
    public String showTripDetails(@PathVariable("id") Integer tripId, Model model, HttpSession session, HttpServletRequest request) {
        CurrentUserDTO currentUser = getCurrentUser(session);
        if (currentUser == null) return "redirect:/login";

        ChuyenDuLich chuyen = chuyenDuLichRepository.findById(tripId).orElse(null);
        if (chuyen == null) return "redirect:/hdvtx/assigned-trips";

        // --- ĐOẠN CODE SỬA LỖI QUAN TRỌNG ---
        int soLuongHanhKhach = chuyenDuLichRepository.getTotalParticipants(tripId);
        List<HanhKhachDTO> danhSachHanhKhach = khachHangRepository.findHanhKhachByMaChuyen(tripId);

        List<LichTrinh> lichTrinh;
        if (chuyen.getTour() != null) {
            // Chỉ lấy lịch trình nếu chuyến đi có tour
            lichTrinh = lichTrinhService.getLichTrinhByTour(chuyen.getTour().getMaTour());
        } else {
            // Nếu không có tour, tạo một danh sách rỗng để tránh lỗi null
            lichTrinh = java.util.Collections.emptyList();
        }
        // --- KẾT THÚC ĐOẠN SỬA LỖI ---

        model.addAttribute("chuyen", chuyen);
        model.addAttribute("soLuongHanhKhach", soLuongHanhKhach);
        model.addAttribute("danhSachHanhKhach", danhSachHanhKhach);
        model.addAttribute("lichTrinh", lichTrinh); // Đảm bảo biến này không bao giờ null
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUri", request.getRequestURI());

        return "/trip-details";
    }

    // ... (các phương thức còn lại)
    
    @PostMapping("/trips/assign/{tripId}")
    public String assignTrip(@PathVariable("tripId") int tripId, RedirectAttributes redirectAttributes, HttpSession session) {
        CurrentUserDTO currentUser = getCurrentUser(session);
        if (currentUser == null) return "redirect:/login";

        try {
            hdvTxService.assignTripToEmployee(tripId, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Nhận chuyến thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }

        return "redirect:/hdvtx/available-trips";
    }

    @PostMapping("/trips/cancel/{tripId}")
    public String cancelTrip(@PathVariable("tripId") int tripId, RedirectAttributes redirectAttributes, HttpSession session) {
        CurrentUserDTO currentUser = getCurrentUser(session);
        if (currentUser == null) return "redirect:/login";

        try {
            hdvTxService.cancelTripAssignment(tripId, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Hủy nhận chuyến thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }

        return "redirect:/hdvtx/assigned-trips";
    }

    private CurrentUserDTO getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || (user.getMaVaiTro() != 3 && user.getMaVaiTro() != 5)) {
            return null;
        }

        Nhanvien nhanvien = nhanvienRepository.findByTaiKhoan_MaTaiKhoan(user.getMaTaiKhoan()).orElse(new Nhanvien());
        return new CurrentUserDTO(user.getHoTen(), nhanvien.getChucVu(), user.getMaVaiTro(), nhanvien.getMaNhanVien());
    }
}
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
import com.example.layout.service.TripExportPdfService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/hdvtx")
public class HdvTxDashboardController {

    @Autowired private HdvTxService hdvTxService;
    @Autowired private NhanvienRepository nhanvienRepository;
    @Autowired private ChuyenDuLichRepository chuyenDuLichRepository;
    @Autowired private KhachHangRepository khachHangRepository;
    @Autowired private LichTrinhService lichTrinhService;
    @Autowired
    private TripExportPdfService tripExportPdfService;
    private static final Logger logger = LoggerFactory.getLogger(HdvTxDashboardController.class);

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
        return "hdvtx/assigned_trips";
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
        return "hdvtx/available_trips";
    }

    @GetMapping("/trip-details/{id}/download")
    public ResponseEntity<byte[]> downloadTripDetails(@PathVariable("id") Integer tripId, HttpSession session) {
        logger.info("=== BẮT ĐẦU TẢI XUỐNG CHI TIẾT CHUYẾN ĐI {} ===", tripId);
        
        try {
            CurrentUserDTO currentUser = getCurrentUser(session);
            if (currentUser == null) {
                return ResponseEntity.status(401).build();
            }

            ChuyenDuLich chuyen = chuyenDuLichRepository.findById(tripId).orElse(null);
            if (chuyen == null) {
                return ResponseEntity.notFound().build();
            }

            int soLuongHanhKhach = chuyenDuLichRepository.getTotalParticipants(tripId);
            List<HanhKhachDTO> danhSachHanhKhach = khachHangRepository.findHanhKhachByMaChuyen(tripId);

            List<LichTrinh> lichTrinh;
            if (chuyen.getTour() != null && chuyen.getTour().getMaTour() != null) {
                lichTrinh = lichTrinhService.getLichTrinhByTour(chuyen.getTour().getMaTour());
            } else {
                lichTrinh = java.util.Collections.emptyList();
            }

            // ✅ Export PDF thay vì Word
            byte[] pdfData = tripExportPdfService.exportTripDetailsToPdf(chuyen, soLuongHanhKhach, danhSachHanhKhach, lichTrinh);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("ChiTietChuyenDi_" + tripId + ".pdf", java.nio.charset.StandardCharsets.UTF_8)
                    .build());

            logger.info("=== TẢI XUỐNG THÀNH CÔNG ===");
            return ResponseEntity.ok().headers(headers).body(pdfData);
            
        } catch (Exception e) {
            logger.error("LỖI khi tải xuống: ", e);
            return ResponseEntity.status(500).build();
        }
    }
    
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
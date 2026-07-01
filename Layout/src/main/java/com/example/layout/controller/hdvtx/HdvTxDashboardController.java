package com.example.layout.controller.hdvtx;

import com.example.layout.dto.HanhKhachDTO;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.LichTrinh;
import com.example.layout.entity.Nhanvien;
import com.example.layout.entity.User;
import com.example.layout.service.HdvTxService;
import com.example.layout.service.IChuyenDuLichService;
import com.example.layout.service.ILichTrinhService;
import com.example.layout.service.INotificationService;
import com.example.layout.service.INhanVienService;
import com.example.layout.utils.VaiTroConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Controller dành cho Hướng dẫn viên / Tài xế.
 *
 * Áp dụng SRP:
 *   - Không còn chứa private method findConflictingTripIds() — dùng hdvTxService.findConflictingTripIds()
 *   - Không còn inject repository trực tiếp.
 *
 * Áp dụng DIP:
 *   - Tất cả dependency đều là interface (HdvTxService sẽ là interface sau khi có I-prefix).
 *   - Dùng VaiTroConstants thay cho magic numbers.
 */
@Controller
@RequestMapping("/hdvtx")
public class HdvTxDashboardController {

    private final HdvTxService         hdvTxService;
    private final IChuyenDuLichService  chuyenDuLichService;
    private final INhanVienService      nhanVienService;
    private final ILichTrinhService     lichTrinhService;
    private final INotificationService  notificationService;

    private static final Logger logger = LoggerFactory.getLogger(HdvTxDashboardController.class);

    public HdvTxDashboardController(HdvTxService hdvTxService,
                                     IChuyenDuLichService chuyenDuLichService,
                                     INhanVienService nhanVienService,
                                     ILichTrinhService lichTrinhService,
                                     INotificationService notificationService) {
        this.hdvTxService        = hdvTxService;
        this.chuyenDuLichService = chuyenDuLichService;
        this.nhanVienService     = nhanVienService;
        this.lichTrinhService    = lichTrinhService;
        this.notificationService = notificationService;
    }

    // =========================================================
    // DTO nội bộ — thông tin người dùng đăng nhập hiện tại
    // =========================================================
    public static class CurrentUserDTO {
        private final String hoTen;
        private final String chucVu;
        private final int    maVaiTro;
        private final int    maNhanVien;

        public CurrentUserDTO(String hoTen, String chucVu, int maVaiTro, int maNhanVien) {
            this.hoTen      = hoTen;
            this.chucVu     = chucVu;
            this.maVaiTro   = maVaiTro;
            this.maNhanVien = maNhanVien;
        }

        public String getHoTen()      { return hoTen; }
        public String getChucVu()     { return chucVu; }
        public int    getMaVaiTro()   { return maVaiTro; }
        public int    getMaNhanVien() { return maNhanVien; }
    }

    // =========================================================
    // Endpoints
    // =========================================================

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session, HttpServletRequest request) {
        CurrentUserDTO currentUser = getCurrentUser(session);
        if (currentUser == null) return "redirect:/login";

        List<ChuyenDuLich> availableTrips = hdvTxService.getAvailableTrips(currentUser);
        List<ChuyenDuLich> assignedTrips  = hdvTxService.getAssignedTrips(currentUser);

        // Dùng INotificationService thay vì inject ChuyenDuLichRepository (DIP)
        List<ChuyenDuLich> upcomingTrips = notificationService.getUpcomingTrips(
                currentUser.getMaNhanVien(), 7);

        model.addAttribute("availableTrips",  availableTrips);
        model.addAttribute("assignedTrips",   assignedTrips);
        model.addAttribute("upcomingTrips",   upcomingTrips);
        model.addAttribute("upcomingCount",   upcomingTrips.size());
        model.addAttribute("currentUser",     currentUser);
        model.addAttribute("currentUri",      request.getRequestURI());

        return "hdvtx/dashboard";
    }

    @GetMapping("/assigned-trips")
    public String showAssignedTrips(
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String tenTour,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            Model model, HttpSession session, HttpServletRequest request) {

        CurrentUserDTO currentUser = getCurrentUser(session);
        if (currentUser == null) return "redirect:/login";

        boolean hasFilter = (trangThai != null && !trangThai.isEmpty())
                || (tenTour != null && !tenTour.isEmpty())
                || tuNgay != null || denNgay != null;

        List<ChuyenDuLich> assignedTrips = hasFilter
                ? hdvTxService.searchAssignedTrips(currentUser, trangThai, tenTour, tuNgay, denNgay)
                : hdvTxService.getAssignedTrips(currentUser);

        // Cập nhật số lượng hành khách qua IChuyenDuLichService (không inject repository)
        assignedTrips.forEach(chuyen ->
                chuyen.setSoLuongHienTai(chuyenDuLichService.getTotalParticipants(chuyen.getMaChuyen())));

        // SRP: dùng hdvTxService để tìm chuyến trùng lịch — không duplicate logic trong controller
        Set<Integer> conflictingTripIds = hdvTxService.findConflictingTripIds(assignedTrips);

        model.addAttribute("assignedTrips",     assignedTrips);
        model.addAttribute("conflictingTripIds", conflictingTripIds);
        model.addAttribute("currentUser",       currentUser);
        model.addAttribute("currentUri",        request.getRequestURI());
        model.addAttribute("filterTrangThai",   trangThai);
        model.addAttribute("filterTenTour",     tenTour);
        model.addAttribute("filterTuNgay",      tuNgay);
        model.addAttribute("filterDenNgay",     denNgay);

        return "hdvtx/assigned_trips";
    }

    @GetMapping("/available-trips")
    public String showAvailableTrips(
            @RequestParam(required = false) String tenTour,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            Model model, HttpSession session, HttpServletRequest request) {

        CurrentUserDTO currentUser = getCurrentUser(session);
        if (currentUser == null) return "redirect:/login";

        List<ChuyenDuLich> availableTrips = (tenTour != null || tuNgay != null || denNgay != null)
                ? hdvTxService.searchAvailableTrips(currentUser, tenTour, tuNgay, denNgay)
                : hdvTxService.getAvailableTrips(currentUser);

        availableTrips.forEach(chuyen ->
                chuyen.setSoLuongHienTai(chuyenDuLichService.getTotalParticipants(chuyen.getMaChuyen())));

        model.addAttribute("availableTrips", availableTrips);
        model.addAttribute("currentUser",    currentUser);
        model.addAttribute("currentUri",     request.getRequestURI());
        model.addAttribute("filterTenTour",  tenTour);
        model.addAttribute("filterTuNgay",   tuNgay);
        model.addAttribute("filterDenNgay",  denNgay);

        return "hdvtx/available_trips";
    }

    @GetMapping("/trip-details/{id}")
    public String showTripDetails(@PathVariable("id") Integer tripId,
                                   Model model, HttpSession session, HttpServletRequest request) {
        logger.info("=== BẮT ĐẦU XEM CHI TIẾT CHUYẾN ĐI {} ===", tripId);
        try {
            CurrentUserDTO currentUser = getCurrentUser(session);
            if (currentUser == null) {
                logger.warn("User chưa đăng nhập, redirect về /login");
                return "redirect:/login";
            }
            logger.info("User đăng nhập: {} - Vai trò: {}", currentUser.getHoTen(), currentUser.getMaVaiTro());

            // Dùng IChuyenDuLichService thay vì inject ChuyenDuLichRepository (DIP)
            ChuyenDuLich chuyen = chuyenDuLichService.getChuyenById(tripId);
            if (chuyen == null) {
                logger.error("KHÔNG TÌM THẤY chuyến đi với ID: {}", tripId);
                return "redirect:/hdvtx/available-trips";
            }

            int soLuongHanhKhach = chuyenDuLichService.getTotalParticipants(tripId);
            logger.info("Số lượng hành khách: {}", soLuongHanhKhach);

            // Dùng IChuyenDuLichService thay vì inject KhachHangRepository (DIP)
            List<HanhKhachDTO> danhSachHanhKhach = chuyenDuLichService.getHanhKhachByMaChuyen(tripId);
            logger.info("Số lượng records hành khách: {}", danhSachHanhKhach.size());

            List<LichTrinh> lichTrinh = Collections.emptyList();
            if (chuyen.getTour() != null && chuyen.getTour().getMaTour() != null) {
                Integer maTour = chuyen.getTour().getMaTour();
                logger.info("Đang lấy lịch trình cho tour ID: {}", maTour);
                try {
                    lichTrinh = lichTrinhService.getLichTrinhByTour(maTour);
                    logger.info("Tìm thấy {} lịch trình", lichTrinh.size());
                } catch (Exception e) {
                    logger.error("LỖI khi lấy lịch trình: ", e);
                }
            } else {
                logger.warn("Chuyến đi không có tour, không lấy lịch trình");
            }

            model.addAttribute("chuyen",            chuyen);
            model.addAttribute("soLuongHanhKhach",  soLuongHanhKhach);
            model.addAttribute("danhSachHanhKhach", danhSachHanhKhach);
            model.addAttribute("lichTrinh",         lichTrinh);
            model.addAttribute("currentUser",       currentUser);
            model.addAttribute("currentUri",        request.getRequestURI());

            logger.info("=== HOÀN THÀNH XỬ LÝ, RENDER VIEW trip-details ===");
            return "trip-details";

        } catch (Exception e) {
            logger.error("LỖI NGHIÊM TRỌNG khi xử lý trip-details: ", e);
            return "redirect:/hdvtx/dashboard";
        }
    }

    @PostMapping("/trips/assign/{tripId}")
    public String assignTrip(@PathVariable("tripId") int tripId,
                              RedirectAttributes redirectAttributes, HttpSession session) {
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
    public String cancelTrip(@PathVariable("tripId") int tripId,
                              RedirectAttributes redirectAttributes, HttpSession session) {
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

    // =========================================================
    // Private helpers
    // =========================================================

    /**
     * Lấy thông tin người dùng hiện tại từ session.
     * Áp dụng OCP: dùng VaiTroConstants thay magic numbers 3, 5.
     */
    private CurrentUserDTO getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null
                || (user.getMaVaiTro() != VaiTroConstants.HUONG_DAN_VIEN
                && user.getMaVaiTro() != VaiTroConstants.TAI_XE)) {
            return null;
        }

        // Dùng INhanVienService thay vì inject NhanvienRepository (DIP)
        Nhanvien nhanvien = nhanVienService.findByMaTaiKhoan(user.getMaTaiKhoan());
        if (nhanvien == null) {
            nhanvien = new Nhanvien();
        }
        return new CurrentUserDTO(
                user.getHoTen(), nhanvien.getChucVu(),
                user.getMaVaiTro(), nhanvien.getMaNhanVien());
    }
}
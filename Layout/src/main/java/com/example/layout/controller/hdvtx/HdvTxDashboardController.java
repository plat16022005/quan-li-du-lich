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
import com.example.layout.service.NotificationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/hdvtx")
public class HdvTxDashboardController {

	@Autowired
	private HdvTxService hdvTxService;
	@Autowired
	private NhanvienRepository nhanvienRepository;
	@Autowired
	private ChuyenDuLichRepository chuyenDuLichRepository;
	@Autowired
	private KhachHangRepository khachHangRepository;
	@Autowired
	private LichTrinhService lichTrinhService;
	private static final Logger logger = LoggerFactory.getLogger(HdvTxDashboardController.class);
	
	@Autowired 
	private NotificationService notificationService;

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

		public String getHoTen() {
			return hoTen;
		}

		public String getChucVu() {
			return chucVu;
		}

		public int getMaVaiTro() {
			return maVaiTro;
		}

		public int getMaNhanVien() {
			return maNhanVien;
		}
	}

	@GetMapping("/dashboard")
	public String showDashboard(Model model, HttpSession session, HttpServletRequest request) {
		CurrentUserDTO currentUser = getCurrentUser(session);
		if (currentUser == null) {
			return "redirect:/login";
		}

		List<ChuyenDuLich> availableTrips = hdvTxService.getAvailableTrips(currentUser);
		List<ChuyenDuLich> assignedTrips = hdvTxService.getAssignedTrips(currentUser);
		
	    LocalDate today = LocalDate.now();
	    LocalDate sevenDaysLater = today.plusDays(7);
	    
	    List<ChuyenDuLich> upcomingTrips = chuyenDuLichRepository.findUpcomingTripsByStaff(
	        today, 
	        sevenDaysLater, 
	        currentUser.getMaNhanVien()
	    );

		model.addAttribute("availableTrips", availableTrips);
		model.addAttribute("assignedTrips", assignedTrips);
	    model.addAttribute("upcomingTrips", upcomingTrips);
	    model.addAttribute("upcomingCount", upcomingTrips.size());
		model.addAttribute("currentUser", currentUser);
		model.addAttribute("currentUri", request.getRequestURI());

		return "hdvtx/dashboard";
	}

	@GetMapping("/assigned-trips")
	public String showAssignedTrips(@RequestParam(required = false) String trangThai,
			@RequestParam(required = false) String tenTour,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
			Model model, HttpSession session, HttpServletRequest request) {

		CurrentUserDTO currentUser = getCurrentUser(session);
		if (currentUser == null)
			return "redirect:/login";

		List<ChuyenDuLich> assignedTrips;

		boolean hasFilter = (trangThai != null && !trangThai.isEmpty()) || (tenTour != null && !tenTour.isEmpty())
				|| tuNgay != null || denNgay != null;

		if (hasFilter) {
			assignedTrips = hdvTxService.searchAssignedTrips(currentUser, trangThai, tenTour, tuNgay, denNgay);
		} else {
			assignedTrips = hdvTxService.getAssignedTrips(currentUser);
		}

		// ✅ Load số lượng hành khách
		assignedTrips.forEach(
				chuyen -> chuyen.setSoLuongHienTai(chuyenDuLichRepository.getTotalParticipants(chuyen.getMaChuyen())));

		// ✅ GIỮ NGUYÊN: Tìm chuyến trùng lịch
		Set<Integer> conflictingTripIds = findConflictingTripIds(assignedTrips);

		model.addAttribute("assignedTrips", assignedTrips);
		model.addAttribute("conflictingTripIds", conflictingTripIds); // ← QUAN TRỌNG
		model.addAttribute("currentUser", currentUser);
		model.addAttribute("currentUri", request.getRequestURI());

		// ✅ Giữ lại giá trị filter
		model.addAttribute("filterTrangThai", trangThai);
		model.addAttribute("filterTenTour", tenTour);
		model.addAttribute("filterTuNgay", tuNgay);
		model.addAttribute("filterDenNgay", denNgay);

		return "hdvtx/assigned_trips";
	}

	/**
	 * ✅ CẬP NHẬT: Hiển thị chuyến cần nhận + Tìm kiếm/Lọc
	 */
	@GetMapping("/available-trips")
	public String showAvailableTrips(@RequestParam(required = false) String tenTour,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
			Model model, HttpSession session, HttpServletRequest request) {

		CurrentUserDTO currentUser = getCurrentUser(session);
		if (currentUser == null)
			return "redirect:/login";

		List<ChuyenDuLich> availableTrips;

		// ✅ Nếu có filter, dùng search
		if (tenTour != null || tuNgay != null || denNgay != null) {
			availableTrips = hdvTxService.searchAvailableTrips(currentUser, tenTour, tuNgay, denNgay);
		} else {
			availableTrips = hdvTxService.getAvailableTrips(currentUser);
		}

		// ✅ Load số lượng hành khách
		availableTrips.forEach(
				chuyen -> chuyen.setSoLuongHienTai(chuyenDuLichRepository.getTotalParticipants(chuyen.getMaChuyen())));

		model.addAttribute("availableTrips", availableTrips);
		model.addAttribute("currentUser", currentUser);
		model.addAttribute("currentUri", request.getRequestURI());

		// ✅ Giữ lại giá trị filter
		model.addAttribute("filterTenTour", tenTour);
		model.addAttribute("filterTuNgay", tuNgay);
		model.addAttribute("filterDenNgay", denNgay);

		return "hdvtx/available_trips";
	}

	@GetMapping("/trip-details/{id}")
	public String showTripDetails(@PathVariable("id") Integer tripId, Model model, HttpSession session,
			HttpServletRequest request) {
		logger.info("=== BẮT ĐẦU XEM CHI TIẾT CHUYẾN ĐI {} ===", tripId);

		try {
			CurrentUserDTO currentUser = getCurrentUser(session);
			if (currentUser == null) {
				logger.warn("User chưa đăng nhập, redirect về /login");
				return "redirect:/login";
			}
			logger.info("User đăng nhập: {} - Vai trò: {}", currentUser.getHoTen(), currentUser.getMaVaiTro());

			ChuyenDuLich chuyen = chuyenDuLichRepository.findById(tripId).orElse(null);
			if (chuyen == null) {
				logger.error("KHÔNG TÌM THẤY chuyến đi với ID: {}", tripId);
				return "redirect:/hdvtx/available-trips";
			}

			// Force load tour nếu có
			if (chuyen.getTour() != null) {
				logger.info("Chuyến có tour: {}", chuyen.getTour().getTenTour());
				chuyen.getTour().getMaTour();
			} else {
				logger.info("Chuyến không có tour (chuyến riêng lẻ)");
			}

			int soLuongHanhKhach = chuyenDuLichRepository.getTotalParticipants(tripId);
			logger.info("Số lượng hành khách: {}", soLuongHanhKhach);

			List<HanhKhachDTO> danhSachHanhKhach = khachHangRepository.findHanhKhachByMaChuyen(tripId);
			logger.info("Số lượng records hành khách: {}", danhSachHanhKhach.size());

			List<LichTrinh> lichTrinh;
			if (chuyen.getTour() != null && chuyen.getTour().getMaTour() != null) {
				Integer maTour = chuyen.getTour().getMaTour();
				logger.info("Đang lấy lịch trình cho tour ID: {}", maTour);
				try {
					lichTrinh = lichTrinhService.getLichTrinhByTour(maTour);
					logger.info("Tìm thấy {} lịch trình", lichTrinh.size());
				} catch (Exception e) {
					logger.error("LỖI khi lấy lịch trình: ", e);
					lichTrinh = java.util.Collections.emptyList();
				}
			} else {
				logger.warn("Chuyến đi không có tour, không lấy lịch trình");
				lichTrinh = java.util.Collections.emptyList();
			}

			if (lichTrinh == null) {
				lichTrinh = java.util.Collections.emptyList();
				logger.warn("lichTrinh was null, setting to empty list");
			}

			logger.info("Đang thêm dữ liệu vào model...");
			model.addAttribute("chuyen", chuyen);
			model.addAttribute("soLuongHanhKhach", soLuongHanhKhach);
			model.addAttribute("danhSachHanhKhach", danhSachHanhKhach);
			model.addAttribute("lichTrinh", lichTrinh);
			model.addAttribute("currentUser", currentUser);
			model.addAttribute("currentUri", request.getRequestURI());

			logger.info("=== HOÀN THÀNH XỬ LÝ, RENDER VIEW trip-details ===");
			return "trip-details";

		} catch (Exception e) {
			logger.error("LỖI NGHIÊM TRỌNG khi xử lý trip-details: ", e);
			e.printStackTrace();
			return "redirect:/hdvtx/dashboard";
		}
	}

	@PostMapping("/trips/assign/{tripId}")
	public String assignTrip(@PathVariable("tripId") int tripId, RedirectAttributes redirectAttributes,
			HttpSession session) {
		CurrentUserDTO currentUser = getCurrentUser(session);
		if (currentUser == null)
			return "redirect:/login";

		try {
			hdvTxService.assignTripToEmployee(tripId, currentUser);
			redirectAttributes.addFlashAttribute("successMessage", "Nhận chuyến thành công!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
		}

		return "redirect:/hdvtx/available-trips";
	}

	@PostMapping("/trips/cancel/{tripId}")
	public String cancelTrip(@PathVariable("tripId") int tripId, RedirectAttributes redirectAttributes,
			HttpSession session) {
		CurrentUserDTO currentUser = getCurrentUser(session);
		if (currentUser == null)
			return "redirect:/login";

		try {
			hdvTxService.cancelTripAssignment(tripId, currentUser);
			redirectAttributes.addFlashAttribute("successMessage", "Hủy nhận chuyến thành công!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
		}

		return "redirect:/hdvtx/assigned-trips";
	}

	/**
	 * ✅ GIỮ NGUYÊN: Method tìm chuyến trùng lịch
	 */
	private Set<Integer> findConflictingTripIds(List<ChuyenDuLich> trips) {
		Set<Integer> conflictIds = new HashSet<>();

		for (int i = 0; i < trips.size(); i++) {
			ChuyenDuLich trip1 = trips.get(i);
			if (trip1.getNgayBatDau() == null || trip1.getNgayKetThuc() == null) {
				continue;
			}

			for (int j = i + 1; j < trips.size(); j++) {
				ChuyenDuLich trip2 = trips.get(j);
				if (trip2.getNgayBatDau() == null || trip2.getNgayKetThuc() == null) {
					continue;
				}

				// Kiểm tra overlap: 2 chuyến trùng lịch nếu:
				// trip1.end >= trip2.start AND trip1.start <= trip2.end
				boolean hasOverlap = !(trip1.getNgayKetThuc().isBefore(trip2.getNgayBatDau())
						|| trip1.getNgayBatDau().isAfter(trip2.getNgayKetThuc()));

				if (hasOverlap) {
					logger.warn("⚠️ Phát hiện trùng lịch: Chuyến {} ({} - {}) <=> Chuyến {} ({} - {})",
							trip1.getMaChuyen(), trip1.getNgayBatDau(), trip1.getNgayKetThuc(), trip2.getMaChuyen(),
							trip2.getNgayBatDau(), trip2.getNgayKetThuc());

					conflictIds.add(trip1.getMaChuyen());
					conflictIds.add(trip2.getMaChuyen());
				}
			}
		}

		return conflictIds;
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
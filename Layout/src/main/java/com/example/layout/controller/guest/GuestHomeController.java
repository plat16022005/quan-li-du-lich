package com.example.layout.controller.guest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.layout.entity.User;
import com.example.layout.entity.ChiTietDatCho;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.DatCho;
import com.example.layout.entity.KhachHang;
import com.example.layout.entity.KhuyenMai;
import com.example.layout.entity.PhanHoi;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.DatChoRepository;
import com.example.layout.repository.KhachHangRepository;
import com.example.layout.repository.KhuyenMaiRepository;
import com.example.layout.repository.PhanHoiRepository;
import com.example.layout.repository.TourRepository;
import com.example.layout.repository.UserRepository;
import com.example.layout.entity.Tour;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class GuestHomeController {
	@Autowired
	private TourRepository tourRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private KhachHangRepository khachHangRepository;
	@Autowired
	private ChuyenDuLichRepository chuyenDuLichRepository;
	@Autowired
	private KhuyenMaiRepository khuyenMaiRepository;
	@Autowired
	private DatChoRepository datChoRepository;
	@Autowired
	private PhanHoiRepository phanHoiRepository;
	@GetMapping("/home")
    public String showHomeForm(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if (user == null)
		{
			return "redirect:/access_denied";
		}
		if (user.getMaVaiTro() != 4)
		{
			return "redirect:/access_denied";
		}
		List<KhuyenMai> khuyenMais = khuyenMaiRepository.findKhuyenMaiDangHieuLuc();
        List<Tour> tours = tourRepository.findAll();
        model.addAttribute("tours", tours);
        model.addAttribute("user", user);
        model.addAttribute("khuyenMais", khuyenMais);
        return "guest/home";
    }
	
	@GetMapping("/home/tour")
	public String listTours(
	        @RequestParam(value = "destination", required = false) String destination,
	        @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
	        @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
	        @RequestParam(value = "startDate", required = false) LocalDate startDate,
	        @RequestParam(value = "endDate", required = false) LocalDate endDate,
	        @RequestParam(value = "page", defaultValue = "0") int page,
	        Model model, HttpSession session
	) {
		User user = (User) session.getAttribute("user");
    	if (user == null)
    	{
    		return "redirect:/access_deniel";
    	}
	    Pageable pageable = PageRequest.of(page, 6);

	    Page<Tour> tours = tourRepository.findToursByFilters(
	            (destination != null && !destination.isEmpty()) ? destination : null,
	            minPrice,
	            maxPrice,
	            startDate,
	            endDate,
	            pageable
	    );

	    model.addAttribute("tours", tours.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", tours.getTotalPages());
	    model.addAttribute("destination", destination);
	    model.addAttribute("minPrice", minPrice);
	    model.addAttribute("maxPrice", maxPrice);
	    model.addAttribute("startDate", startDate);
	    model.addAttribute("endDate", endDate);
	    model.addAttribute("user", user);
	    return "guest/all_tour";
	}
	@GetMapping("/profile")
	public String showProfileForm(HttpSession session, Model model)
	{
		User user = (User) session.getAttribute("user");

		if (user == null)
		{
			return "redirect:/access_denied";
		}
		KhachHang kh = khachHangRepository.findByTaiKhoan_MaTaiKhoan(user.getMaTaiKhoan());
		if (user.getMaVaiTro() != 4)
		{
			return "redirect:/access_denied";
		}
		List<ChuyenDuLich> completedTrips = chuyenDuLichRepository.findChuyenDaHoanThanh(kh.getMaKhachHang());
		List<DatCho> upcomingTrips = datChoRepository.findVeSapDienRa(kh.getMaKhachHang());
		List<ChuyenDuLich> upcomingTripsNotPaid = chuyenDuLichRepository.findChuyenSapDienRaChuaThanhToan(kh.getMaKhachHang());
		List<DatCho> unpaidBookings = datChoRepository.findDatChoSapDienRaChuaThanhToan(kh.getMaKhachHang());

		List<Integer> ratedTripIds = phanHoiRepository.findMaChuyenDaDanhGia(kh.getMaKhachHang());
	    model.addAttribute("ratedTripIds", ratedTripIds);
        model.addAttribute("completedTrips", completedTrips);
        model.addAttribute("upcomingTrips", upcomingTrips);
        model.addAttribute("upcomingTripsNotPaid", upcomingTripsNotPaid);
        model.addAttribute("unpaidBookings", unpaidBookings);
		model.addAttribute("kh", kh);
        model.addAttribute("user", user);
        return "guest/profile";
	}
	
	@PostMapping("/profile/update")
	@ResponseBody
	public ResponseEntity<String> updateProfile(@RequestBody Map<String, String> updates, HttpSession session) {
	    User currentUser = (User) session.getAttribute("user");

	    if (currentUser == null) return ResponseEntity.status(401).body("Chưa đăng nhập");
	    KhachHang kh = khachHangRepository.findByTaiKhoan_MaTaiKhoan(currentUser.getMaTaiKhoan());
	    if (updates.containsKey("hoTen")) currentUser.setHoTen(updates.get("hoTen"));
	    if (updates.containsKey("email")) currentUser.setEmail(updates.get("email"));
	    if (updates.containsKey("soDienThoai")) currentUser.setSoDienThoai(updates.get("soDienThoai"));
	    if (updates.containsKey("ngaySinh")) kh.setNgaySinh(LocalDate.parse(updates.get("ngaySinh")));
	    if (updates.containsKey("diaChi")) kh.setDiaChi(updates.get("diaChi"));

	    userRepository.save(currentUser);
	    khachHangRepository.save(kh);
	    session.setAttribute("user", currentUser);
	    return ResponseEntity.ok("OK");
	}
	@GetMapping("/ticket/detail/{maDatCho}")
	public String showTicketDetail(@PathVariable Integer maDatCho, Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");

		if (user == null)
		{
			return "redirect:/access_denied";
		}
	    DatCho datCho = datChoRepository.findById(maDatCho)
	        .orElseThrow(() -> new RuntimeException("Không tìm thấy vé"));
	    ChuyenDuLich chuyen = chuyenDuLichRepository.findChuyenByMaDatCho(datCho.getMaDatCho());
	    ChiTietDatCho chiTiet = datCho.getChiTietDatChos()
	    	    .stream()
	    	    .findFirst()
	    	    .orElse(null);
	    model.addAttribute("chitiet", chiTiet);
	    model.addAttribute("datCho", datCho);
	    model.addAttribute("chuyen", chuyen);
	    model.addAttribute("user", user);
	    return "guest/ticket_detail";
	}
	@GetMapping("/review/{maChuyen}")
	public String showReview(@PathVariable Integer maChuyen, Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null)
		{
			return "redirect:/access_denied";
		}
		ChuyenDuLich chuyen = chuyenDuLichRepository.findById(maChuyen)
			    .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến du lịch có mã: " + maChuyen));
		model.addAttribute("chuyen", chuyen);
		model.addAttribute("user", user);
		return "guest/danhgia";
	}
	@PostMapping("/do-rating")
	public String handleRating(@RequestParam Integer rating,
	                           @RequestParam String review_content,
	                           @RequestParam Integer maChuyen,
	                           HttpSession session) {
	    User user = (User) session.getAttribute("user");
	    if (user == null) return "redirect:/access_denied";
	    KhachHang kh = khachHangRepository.findByTaiKhoan_MaTaiKhoan(user.getMaTaiKhoan());
	    // ✅ Lấy ra chuyến đang được đánh giá
	    ChuyenDuLich chuyen = chuyenDuLichRepository.findById(maChuyen)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến"));

	    PhanHoi phanhoi = new PhanHoi();
	    phanhoi.setKhachHang(kh);
	    phanhoi.setDanhGia(rating);
	    phanhoi.setChuyenDuLich(chuyen);
	    phanhoi.setNoiDung(review_content);
	    phanhoi.setNgayTao(LocalDateTime.now());
	    phanHoiRepository.save(phanhoi);
	    return "redirect:/profile";
	}
	@PostMapping("/do-changepass")
	public String handleChangePassword(@RequestParam String current_password,
	                                   @RequestParam String new_password,
	                                   @RequestParam String confirm_password,
	                                   HttpSession session,
	                                   RedirectAttributes redirectAttributes) {
	    User user = (User) session.getAttribute("user");

	    if (user == null) {
	        redirectAttributes.addFlashAttribute("errorPass", "Bạn cần đăng nhập để đổi mật khẩu.");
	        return "redirect:/login";
	    }

	    // 🔐 1. Kiểm tra mật khẩu hiện tại
	    if (!current_password.equals(user.getMatKhau())) {
	        redirectAttributes.addFlashAttribute("errorPass", "Mật khẩu hiện tại không đúng!");
	        return "redirect:/profile";
	    }

	    // 🧾 2. Kiểm tra mật khẩu mới trùng xác nhận
	    if (!new_password.equals(confirm_password)) {
	        redirectAttributes.addFlashAttribute("errorPass", "Mật khẩu xác nhận không trùng khớp!");
	        return "redirect:/profile";
	    }

	    // ⚠️ 3. Không cho đặt lại giống mật khẩu cũ
	    if (new_password.equals(current_password)) {
	        redirectAttributes.addFlashAttribute("errorPass", "Mật khẩu mới không được trùng mật khẩu cũ!");
	        return "redirect:/profile";
	    }

	    // 💾 4. Cập nhật mật khẩu
	    user.setMatKhau(confirm_password); // Thực tế nên mã hoá trước khi lưu
	    userRepository.save(user);

	    redirectAttributes.addFlashAttribute("successPass", "Đổi mật khẩu thành công!");
	    return "redirect:/profile";
	}

}

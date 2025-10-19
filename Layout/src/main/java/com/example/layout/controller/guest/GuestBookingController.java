package com.example.layout.controller.guest;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.KhachHang;
import com.example.layout.entity.Tour;
import com.example.layout.entity.User;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.KhachHangRepository;
import com.example.layout.repository.KhuyenMaiRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class GuestBookingController {
	
	@Autowired
	private ChuyenDuLichRepository chuyenDuLichRepository;
	
	@Autowired
	private KhachHangRepository khachHangRepository;
	
	@Autowired
	private KhuyenMaiRepository khuyenMaiRepository;
	
	@GetMapping("/tour/booking/{maChuyen}")
	public String bookingPage(@PathVariable("maChuyen") Integer maChuyen, Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
	    // Tìm chuyến
	    ChuyenDuLich chuyen = chuyenDuLichRepository.findById(maChuyen)
	        .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến ID: " + maChuyen));
	    
	    // Tìm chuyến
	    KhachHang khachhang = khachHangRepository.findByTaiKhoan_MaTaiKhoan(user.getMaTaiKhoan());

	    // Lấy tour liên quan
	    Tour tour = chuyen.getTour();

	    model.addAttribute("chuyen", chuyen);
	    model.addAttribute("tour", tour);
	    model.addAttribute("user", user);
	    model.addAttribute("khachhang", khachhang);
	    return "guest/giucho";
	}

	@GetMapping("/tour/booking/{maChuyen}/submit")
	public String submitPage(@PathVariable("maChuyen") Integer maChuyen,
	                         Model model,
	                         HttpSession session,
	                         @RequestParam(required = false) String notes,
	                         @RequestParam int adults,
	                         @RequestParam int children,
	                         @RequestParam(required = false) String discountCode) {

	    User user = (User) session.getAttribute("user");

	    ChuyenDuLich chuyen = chuyenDuLichRepository.findById(maChuyen)
	        .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến ID: " + maChuyen));

	    KhachHang khachhang = khachHangRepository.findByTaiKhoan_MaTaiKhoan(user.getMaTaiKhoan());

	    Tour tour = chuyen.getTour();
	    BigDecimal giaCoBan = tour.getGiaCoBan();

	    if (giaCoBan == null) {
	        throw new RuntimeException("Tour chưa có giá cơ bản!");
	    }

	    BigDecimal adultsBD = BigDecimal.valueOf(adults);
	    BigDecimal childrenBD = BigDecimal.valueOf(children);
	    BigDecimal tiLeTreEm = BigDecimal.valueOf(0.2); // 20%

	    BigDecimal giaNguoiLon = giaCoBan.multiply(adultsBD);
	    BigDecimal giaTreEm = giaCoBan.multiply(childrenBD).multiply(tiLeTreEm);
	    BigDecimal giaGoc = giaNguoiLon.add(giaTreEm);

	    BigDecimal tiLeGiam = BigDecimal.ZERO;
	    if (discountCode != null && !discountCode.isBlank()) {
	        BigDecimal tiLeDB = khuyenMaiRepository.findTiLeGiamByMaCode(discountCode);
	        if (tiLeDB != null) {
	            tiLeGiam = tiLeDB;
	        }
	    }

	    BigDecimal giamGia = giaGoc.multiply(tiLeGiam)
	            .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
	    BigDecimal thanhTien = giaGoc.subtract(giamGia);

	    model.addAttribute("soNguoiLon", adults);
	    model.addAttribute("soTreEm", children);
	    model.addAttribute("giaGoc", giaGoc);
	    model.addAttribute("giamGia", giamGia);
	    model.addAttribute("thanhTien", thanhTien);
	    model.addAttribute("chuyen", chuyen);
	    model.addAttribute("tour", tour);
	    model.addAttribute("user", user);
	    model.addAttribute("khachhang", khachhang);

	    return "guest/thanhtoan";
	}

}

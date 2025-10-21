package com.example.layout.controller.guest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.layout.entity.ChiTietDatCho;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.DatCho;
import com.example.layout.entity.KhachHang;
import com.example.layout.entity.ThanhToan;
import com.example.layout.entity.Tour;
import com.example.layout.entity.User;
import com.example.layout.repository.ChiTietDatChoRepository;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.DatChoRepository;
import com.example.layout.repository.KhachHangRepository;
import com.example.layout.repository.KhuyenMaiRepository;
import com.example.layout.repository.ThanhToanRepository;
import com.example.layout.repository.TourRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class GuestBookingController {
	
	@Autowired
	private ChuyenDuLichRepository chuyenDuLichRepository;

	@Autowired
	private TourRepository tourRepository;
	
	@Autowired
	private KhachHangRepository khachHangRepository;
	
	@Autowired
	private ChiTietDatChoRepository chiTietDatChoRepository;
	
	@Autowired
	private KhuyenMaiRepository khuyenMaiRepository;
	
	@Autowired
	private DatChoRepository datChoRepository;
	
	@Autowired
	private ThanhToanRepository thanhToanRepository;
	
	@GetMapping("/tour/booking/{maChuyen}")
	public String bookingPage(@PathVariable("maChuyen") Integer maChuyen, Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null)
		{
			return "redirect:/access_denied";
		}
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
    	if (user == null)
    	{
    		return "redirect:/access_deniel";
    	}

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
	    
	    model.addAttribute("notes", notes);
	    model.addAttribute("soNguoiLon", adults);
	    model.addAttribute("soTreEm", children);
	    model.addAttribute("giaGoc", giaGoc);
	    model.addAttribute("giamGia", giamGia);
	    model.addAttribute("tiLeGiam", tiLeGiam);
	    model.addAttribute("thanhTien", thanhTien);
	    model.addAttribute("chuyen", chuyen);
	    model.addAttribute("tour", tour);
	    model.addAttribute("user", user);
	    model.addAttribute("khachhang", khachhang);

	    return "guest/thanhtoan";
	}
	@GetMapping("/tour/booking/{maChuyen}/submit/payment")
	public String choosePayment(@RequestParam String paymentMethod, @PathVariable("maChuyen") Integer maChuyen, HttpSession session) {
		User user = (User) session.getAttribute("user");
    	if (user == null)
    	{
    		return "redirect:/access_deniel";
    	}
	    switch (paymentMethod) {
	        case "bank":
	            return "guest/payment1";
	        case "ewallet":
	            return "guest/payment2";
	        case "credit":
	            return "guest/payment3";
	    }
	    return "/tour/booking/{maChuyen}/submit";
	}
	@PostMapping("/payment/confirm")
	public String handlePaid(@RequestParam Integer khachHangId,
							@RequestParam Integer chuyenId,
							@RequestParam BigDecimal tiLeGiam,
							@RequestParam String submitType,
							@RequestParam BigDecimal thanhTien,
							@RequestParam String paymentMethod,
							@RequestParam Integer adults,
							@RequestParam Integer children,
							@RequestParam String notes,
							HttpSession session)
	{
		
		DatCho dt = new DatCho();
		KhachHang kh = khachHangRepository.findById(khachHangId).orElseThrow(() -> new RuntimeException("Không tìm thấy: " + khachHangId));
		ChuyenDuLich dcl = chuyenDuLichRepository.findById(chuyenId).orElseThrow(() -> new RuntimeException("Không tìm thấy: " + chuyenId));
		Tour tour = tourRepository.findById(dcl.getTour().getMaTour()).orElseThrow(() -> new RuntimeException("Không tìm thấy: " + chuyenId));;
		dt.setKhachHang(kh);
		dt.setChuyenDuLich(dcl);
		dt.setNgayDat(LocalDate.now());
		dt.setTiLeGiam(tiLeGiam);
		if (submitType.equals("paid"))
			dt.setTrangThai("Đã thanh toán");
		else
			dt.setTrangThai("Chưa thanh toán");
		datChoRepository.save(dt);
		
		ChiTietDatCho ctdc = new ChiTietDatCho();
		ctdc.setDatCho(dt);
		ctdc.setSoLuong(adults + children);
		ctdc.setDonGia(tour.getGiaCoBan());
		if (submitType.equals("paid"))
			ctdc.setThanhTien(thanhTien);
		else
			ctdc.setThanhTien(thanhTien.multiply(new BigDecimal(1.05)));
		ctdc.setLoaiVe("Thường");
		ctdc.setNguoiLon(adults);
		ctdc.setTreCon(children);
		ctdc.setYeuCau(notes);
		chiTietDatChoRepository.save(ctdc);
		if (!submitType.equals("paid"))
			return "redirect:/home";
		else
		{
			ThanhToan tt = new ThanhToan();
			tt.setDatCho(dt);
			tt.setSoTien(thanhTien);
			tt.setHinhThuc(paymentMethod);
			tt.setNgayThanhToan(LocalDate.now());
			thanhToanRepository.save(tt);
			return "redirect:/profile";
		}
	}
}

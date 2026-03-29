package com.example.layout.controller.manager.guest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.layout.entity.KhachHang;
import com.example.layout.entity.User;
import com.example.layout.service.IKhachHangService;
import com.example.layout.service.IPhanHoiService;
import com.example.layout.utils.VaiTroConstants;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/manager")
public class ManagerGuestController {
    private final IPhanHoiService phanHoiService;
    private final IKhachHangService khachHangService;

    public ManagerGuestController(IPhanHoiService phanHoiService, IKhachHangService khachHangService) {
        this.phanHoiService = phanHoiService;
        this.khachHangService = khachHangService;
    }

	@GetMapping("/guest")
    public String showGuestForm(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null || user.getMaVaiTro() != VaiTroConstants.ADMIN)
		{
			return "redirect:/access_denied";
		}
        return "manager/guest";
    }
	@GetMapping("/guest/feedback")
	@ResponseBody
	public List<Map<String, Object>> getFeedbackByCustomer(@RequestParam("customerId") int customerId) {
	    return phanHoiService.getFeedbackByCustomer(customerId);
	}
	@GetMapping("/guest/with-feedback")
	@ResponseBody
	public Map<String, Object> getCustomersWithFeedback(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {
        return phanHoiService.getCustomersWithFeedback(page, size);
	}


	@GetMapping("/guest/info")
	@ResponseBody
	public Map<String, Object> getCustomerInfo(@RequestParam("customerId") int customerId) {
	    KhachHang kh = khachHangService.findById(customerId).orElse(null);
	    Map<String, Object> result = new HashMap<>();

	    if (kh != null) {
	        User u = kh.getTaiKhoan();
	        result.put("name", u.getHoTen());
	        result.put("email", u.getEmail());
	        result.put("phone", u.getSoDienThoai());
	        result.put("address", kh.getDiaChi());
	        result.put("createdAt", kh.getNgayThamGia() != null ? kh.getNgayThamGia().toString() : null);
	    }

	    return result;
	}

}

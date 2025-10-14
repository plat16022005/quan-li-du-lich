package com.example.layout.controller.nhanvien;

import com.example.layout.entity.DatCho;
import com.example.layout.entity.KhachHang;
import com.example.layout.service.DatChoService;
import com.example.layout.service.KhachHangService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/nhanvien/customers")
public class NhanvienCustomerController {

    private final KhachHangService khachHangService;
    private final DatChoService datChoService;
    
    
    public NhanvienCustomerController(KhachHangService khachHangService,DatChoService datChoService) {
        this.khachHangService = khachHangService;
		this.datChoService = datChoService;
    } 

    @GetMapping
    public String showCustomerPage(Model model) {
        return "nhanvien/customer/list"; 
    }

    /**
     * API: Cung cấp dữ liệu khách hàng dưới dạng JSON để JavaScript sử dụng.
     */
    @GetMapping("/api")
    @ResponseBody 
    public ResponseEntity<Page<KhachHang>> getCustomersApi(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "maKhachHang") Pageable pageable) {
        
        Page<KhachHang> customerPage = khachHangService.search(keyword, pageable);
        return ResponseEntity.ok(customerPage);
    }
    
    @GetMapping("/{id}")
    public String showCustomerDetailPage(@PathVariable("id") Integer customerId, Model model) {
        model.addAttribute("customerId", customerId);
        return "nhanvien/customer/detail";
    }

    /**
     * API: Cung cấp dữ liệu JSON chi tiết cho một khách hàng.
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCustomerDetailsApi(@PathVariable("id") Integer customerId) {
        KhachHang customer = khachHangService.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        List<DatCho> bookingHistory = datChoService.findByKhachHangId(customerId);

        Map<String, Object> response = new HashMap<>();
        response.put("customer", customer);
        response.put("bookingHistory", bookingHistory);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer customerId, Model model) {
        KhachHang customer = khachHangService.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer Id:" + customerId));
        model.addAttribute("customer", customer);
        return "nhanvien/customer/form"; // View: /templates/nhanvien/customer/form.html
    }
    
    @PostMapping("/update")
    public String updateCustomer(@ModelAttribute("customer") KhachHang customer,
                                RedirectAttributes redirectAttributes) {
        khachHangService.save(customer);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin khách hàng thành công!");
        return "redirect:/nhanvien/customers/" + customer.getMaKhachHang();
    }
    
}
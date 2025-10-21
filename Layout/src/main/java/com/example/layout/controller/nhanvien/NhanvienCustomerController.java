package com.example.layout.controller.nhanvien;

import com.example.layout.dto.BookingHistoryDTO;
import com.example.layout.dto.CustomerDetailDTO;
import com.example.layout.dto.NewCustomerDTO;
import com.example.layout.entity.DatCho;
import com.example.layout.entity.KhachHang;
import com.example.layout.entity.User;
import com.example.layout.repository.KhachHangRepository;
import com.example.layout.service.DatChoService;
import com.example.layout.service.KhachHangService;
import com.example.layout.service.UserService;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final UserService userService;
    private final KhachHangRepository khachHangRepository;
    
    public NhanvienCustomerController(KhachHangService khachHangService, DatChoService datChoService, UserService userService, KhachHangRepository khachHangRepository) {
        this.khachHangService = khachHangService;
		this.datChoService = datChoService;
		this.userService = userService;
		this.khachHangRepository = khachHangRepository;
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
    public String showCustomerDetailPage(@PathVariable("id") Integer customerId, Model model, HttpSession session) {
    	User user = (User) session.getAttribute("user");
    	if (user == null)
    	{
    		return "redirect:/access_deniel";
    	}
        model.addAttribute("customerId", customerId);
        System.out.println("Customer ID: " + customerId); // Debug log
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
        List<DatCho> bookingHistoryEntities = datChoService.findByKhachHangId(customerId);

        CustomerDetailDTO customerDTO = new CustomerDetailDTO(customer);
        List<BookingHistoryDTO> bookingHistoryDTO = bookingHistoryEntities.stream()
            .map(BookingHistoryDTO::new) // Sử dụng constructor
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("customer", customerDTO); // Trả về DTO
        response.put("bookingHistory", bookingHistoryDTO); // Trả về DTO

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
    
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("newCustomer", new NewCustomerDTO());
        return "nhanvien/customer/add"; // View: /templates/nhanvien/customer/add.html
    }
    
    @PostMapping("/save-new")
    public String saveNewCustomer(@ModelAttribute("newCustomer") NewCustomerDTO newCustomerDTO,
                                RedirectAttributes redirectAttributes) {
        try {
            // Tạo tài khoản mới
            boolean userCreated = userService.register(
                newCustomerDTO.getEmail(), // Sử dụng email làm username
                "123456", // Mật khẩu mặc định
                newCustomerDTO.getHoTen(),
                newCustomerDTO.getEmail(),
                newCustomerDTO.getSoDienThoai()
            );
            
            if (!userCreated) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email đã tồn tại trong hệ thống!");
                return "redirect:/nhanvien/customers/add";
            }
            
            // Lấy User vừa tạo
            User user = userService.getConfirm(newCustomerDTO.getEmail());
            
            // Tạo KhachHang
            KhachHang khachHang = new KhachHang();
            khachHang.setTaiKhoan(user);
            khachHang.setDiaChi(newCustomerDTO.getDiaChi());
            khachHang.setNgaySinh(newCustomerDTO.getNgaySinh());
            khachHang.setGioiTinh(newCustomerDTO.getGioiTinh());
            khachHang.setBietDen(newCustomerDTO.getBietDen());
            khachHang.setNgayThamGia(LocalDate.now());
            
            khachHangService.save(khachHang);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm khách hàng mới thành công!");
            return "redirect:/nhanvien/customers";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi thêm khách hàng: " + e.getMessage());
            return "redirect:/nhanvien/customers/add";
        }
    }
    
    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute("customer") KhachHang customer,
                            RedirectAttributes redirectAttributes) {
        khachHangService.save(customer);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm khách hàng mới thành công!");
        return "redirect:/nhanvien/customers";
    }
    
}

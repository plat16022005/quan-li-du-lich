package com.example.layout.controller.nhanvien;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.layout.dto.DatChoDTO;
import com.example.layout.dto.ThanhToanDTO;
import com.example.layout.entity.DatCho;
import com.example.layout.entity.ThanhToan;
import com.example.layout.service.ChuyenDuLichService;
import com.example.layout.service.DatChoService;
import com.example.layout.service.KhachHangService;
import com.example.layout.service.ThanhToanService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/nhanvien/bookings")
public class NhanvienBookingController {
    private static final Logger logger = LoggerFactory.getLogger(NhanvienBookingController.class);
	private final DatChoService datChoService;
    private final ThanhToanService thanhToanService;
    private final KhachHangService khachHangService;
    private final ChuyenDuLichService chuyenDuLichService;

    public NhanvienBookingController(DatChoService datChoService, ThanhToanService thanhToanService,
    							KhachHangService khachHangService, ChuyenDuLichService chuyenDuLichService) {
        this.datChoService = datChoService;
		this.thanhToanService = thanhToanService;
		this.khachHangService = khachHangService;
		this.chuyenDuLichService = chuyenDuLichService;
    }

    @GetMapping
    public String showBookingListPage() {
        return "nhanvien/booking/list";
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Page<com.example.layout.dto.BookingApiDTO>> getBookingsApi(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "ngayDat") Pageable pageable) {
        Page<com.example.layout.dto.BookingApiDTO> bookingPage = datChoService.searchAndFilterDto(keyword, status, pageable);
        return ResponseEntity.ok(bookingPage);
    }
    
    @GetMapping("/{id}")
    public String showBookingDetailPage(@PathVariable("id") Integer bookingId, Model model) {
        model.addAttribute("bookingId", bookingId); 
        return "nhanvien/booking/detail";
    }
    
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBookingDetailsApi(@PathVariable("id") Integer bookingId) {
        // Return DTO to avoid lazy-loading exceptions during JSON serialization
    com.example.layout.dto.BookingApiDTO bookingDto = datChoService.getBookingApiDtoById(bookingId);
    List<com.example.layout.dto.ThanhToanDTO> paymentHistory = thanhToanService.findDtoByDatChoId(bookingId);

        Map<String, Object> response = new HashMap<>();
        response.put("booking", bookingDto);
        response.put("paymentHistory", paymentHistory);

        return ResponseEntity.ok(response);
    }
    

    
    
    @GetMapping("/add")
    public String showAddBookingForm(Model model) {
        model.addAttribute("datChoDTO", new DatChoDTO());
        // Load danh sách khách hàng và các chuyến đi sắp diễn ra để chọn
        model.addAttribute("customerList", khachHangService.findAll());
        model.addAttribute("tripList", chuyenDuLichService.findByTrangThai("Sắp diễn ra"));
        return "nhanvien/booking/form";
    }

    //Xử lý việc tạo mới một đơn đặt chỗ.
    @PostMapping("/save")
    public String saveBooking(@Valid @ModelAttribute("datChoDTO") DatChoDTO datChoDTO,
                              BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("customerList", khachHangService.findAll());
            model.addAttribute("tripList", chuyenDuLichService.findByTrangThai("Sắp diễn ra"));
            return "nhanvien/booking/form";
        }
        try {
            DatCho newBooking = datChoService.createBooking(datChoDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo đơn đặt chỗ thành công!");
            return "redirect:/nhanvien/bookings/" + newBooking.getMaDatCho();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/nhanvien/bookings/add";
        }
    }


    //: Xử lý việc hủy một đơn đặt chỗ.
    @GetMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable("id") Integer bookingId, RedirectAttributes redirectAttributes) {
        try {
            datChoService.cancelBooking(bookingId);
            redirectAttributes.addFlashAttribute("successMessage", "Hủy đơn đặt chỗ thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/nhanvien/bookings/" + bookingId;
    }
    
    
    @PostMapping("/api/payment/record")
    @ResponseBody
    public ResponseEntity<?> recordPaymentApi(@Valid @RequestBody ThanhToanDTO thanhToanDTO, BindingResult result) {
        if (result.hasErrors()) {
            // Trả về lỗi validation nếu dữ liệu không hợp lệ
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        try {
            ThanhToan newPayment = thanhToanService.recordPayment(thanhToanDTO);
            // Trả về đối tượng thanh toán vừa tạo thành công
            return ResponseEntity.ok(newPayment);
        } catch (RuntimeException e) {
            // Trả về lỗi nghiệp vụ (ví dụ: đơn đặt chỗ không tồn tại)
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET MAPPING: Xử lý việc xác nhận một đơn đặt chỗ.
     */
    @GetMapping("/confirm/{id}")
    public String confirmBooking(@PathVariable("id") Integer bookingId, RedirectAttributes redirectAttributes) {
        try {
            // Bạn cần thêm phương thức confirmBooking vào DatChoService
            datChoService.confirmBooking(bookingId);
            redirectAttributes.addFlashAttribute("successMessage", "Xác nhận đơn đặt chỗ thành công!");
        } catch (Exception e) {
            // Log full stacktrace for diagnostics and surface a user-friendly flash message
            logger.error("Lỗi khi xác nhận đơn đặt chỗ id={}", bookingId, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage() != null ? e.getMessage() : "Đã xảy ra lỗi khi xác nhận đơn đặt chỗ.");
        }
        // Sau khi xác nhận, quay lại trang chi tiết
        return "redirect:/nhanvien/bookings/" + bookingId;
    }
    
}

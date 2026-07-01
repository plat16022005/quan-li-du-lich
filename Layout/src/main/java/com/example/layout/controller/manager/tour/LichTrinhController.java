package com.example.layout.controller.manager.tour;

import com.example.layout.entity.LichTrinh;
import com.example.layout.service.IDiaDiemService;
import com.example.layout.service.IKhachSanService;
import com.example.layout.service.ILichTrinhService;
import com.example.layout.service.IPhuongTienService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller quản lý lịch trình.
 * Áp dụng SRP: controller chỉ xử lý HTTP request/response,
 * toàn bộ business logic (bao gồm addMultiple) đã được chuyển vào ILichTrinhService.
 * Áp dụng DIP: phụ thuộc vào interface, không inject repository trực tiếp.
 */
@RestController
@RequestMapping("/manager/lichtrinh")
public class LichTrinhController {

    private final ILichTrinhService lichTrinhService;
    private final IDiaDiemService   diaDiemService;
    private final IKhachSanService  khachSanService;
    private final IPhuongTienService phuongTienService;

    public LichTrinhController(ILichTrinhService lichTrinhService,
                               IDiaDiemService diaDiemService,
                               IKhachSanService khachSanService,
                               IPhuongTienService phuongTienService) {
        this.lichTrinhService  = lichTrinhService;
        this.diaDiemService    = diaDiemService;
        this.khachSanService   = khachSanService;
        this.phuongTienService = phuongTienService;
    }

    /**
     * Thêm nhiều lịch trình cho một tour.
     * Logic đã được chuyển vào LichTrinhService (SRP).
     */
    @PostMapping("/add-multiple")
    public ResponseEntity<?> addMultiple(@RequestBody List<ILichTrinhService.LichTrinhRequest> lichTrinhList,
                                         HttpSession session) {
        String maTourSession = (String) session.getAttribute("matour");
        if (maTourSession == null) {
            return ResponseEntity.badRequest().body("❌ Không tìm thấy mã tour trong session");
        }

        try {
            Integer maTour = Integer.parseInt(maTourSession);
            lichTrinhService.addMultiple(maTour, lichTrinhList);
            return ResponseEntity.ok("✅ Lưu thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    @GetMapping("/tour/{maTour}")
    @ResponseBody
    public ResponseEntity<List<LichTrinh>> getLichTrinhByTour(@PathVariable Integer maTour) {
        List<LichTrinh> lichTrinhList = lichTrinhService.getLichTrinhByTourOrderByNgay(maTour);
        return ResponseEntity.ok(lichTrinhList);
    }

    @GetMapping("/form-data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFormData() {
        Map<String, Object> response = new HashMap<>();
        response.put("diaDiemList",   diaDiemService.findAll());
        response.put("khachSanList",  khachSanService.findAll());
        response.put("phuongTienList", phuongTienService.findAll());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<LichTrinh> createLichTrinh(@RequestBody LichTrinh lichTrinh) {
        LichTrinh savedLichTrinh = lichTrinhService.saveLichTrinh(lichTrinh);
        return ResponseEntity.ok(savedLichTrinh);
    }

    @PutMapping("/save/{maLichTrinh}")
    @ResponseBody
    public ResponseEntity<LichTrinh> updateLichTrinh(@PathVariable Integer maLichTrinh,
                                                      @RequestBody LichTrinh updatedData) {
        LichTrinh saved = lichTrinhService.update(maLichTrinh, updatedData);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/delete/{maLichTrinh}")
    @ResponseBody
    public ResponseEntity<?> deleteLichTrinh(@PathVariable Integer maLichTrinh) {
        try {
            lichTrinhService.deleteById(maLichTrinh);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Xóa thất bại");
        }
    }
}

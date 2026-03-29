package com.example.layout.controller.manager.tour;

import com.example.layout.entity.KhachSan;
import com.example.layout.service.IKhachSanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager/khachsan")
public class KhachSanController {

    private final IKhachSanService khachSanService;

    public KhachSanController(IKhachSanService khachSanService) {
        this.khachSanService = khachSanService;
    }

    // 📋 Lấy toàn bộ khách sạn
    @GetMapping("/all")
    public List<KhachSan> getAll() {
        return khachSanService.findAll();
    }

    // ➕ Thêm khách sạn mới
    @PostMapping("/add")
    public ResponseEntity<?> addKhachSan(@RequestBody KhachSan ks) {
        khachSanService.save(ks);
        return ResponseEntity.ok("Thêm khách sạn thành công");
    }

    // ✏️ Cập nhật thông tin khách sạn
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateKhachSan(@PathVariable Integer id, @RequestBody KhachSan newKs) {
        KhachSan ks = khachSanService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách sạn"));
        ks.setTenKhachSan(newKs.getTenKhachSan());
        ks.setDiaChi(newKs.getDiaChi());
        ks.setSoDienThoai(newKs.getSoDienThoai());
        khachSanService.save(ks);
        return ResponseEntity.ok("Cập nhật khách sạn thành công");
    }

    // 🗑 Xóa khách sạn
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteKhachSan(@PathVariable Integer id) {
        khachSanService.deleteById(id);
        return ResponseEntity.ok("Xóa khách sạn thành công");
    }
}

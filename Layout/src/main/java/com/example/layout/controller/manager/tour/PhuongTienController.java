package com.example.layout.controller.manager.tour;

import com.example.layout.entity.PhuongTien;
import com.example.layout.service.IPhuongTienService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager/phuongtien")
public class PhuongTienController {

    private final IPhuongTienService phuongTienService;

    public PhuongTienController(IPhuongTienService phuongTienService) {
        this.phuongTienService = phuongTienService;
    }

    // 📋 Lấy danh sách phương tiện
    @GetMapping("/all")
    public List<PhuongTien> getAll() {
        return phuongTienService.findAll();
    }

    // ➕ Thêm phương tiện mới
    @PostMapping("/add")
    public ResponseEntity<?> addPhuongTien(@RequestBody PhuongTien pt) {
        phuongTienService.save(pt);
        return ResponseEntity.ok("Thêm phương tiện thành công");
    }

    // ✏️ Cập nhật phương tiện
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePhuongTien(@PathVariable Integer id, @RequestBody PhuongTien newPt) {
        PhuongTien pt = phuongTienService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương tiện"));
        pt.setLoaiPhuongTien(newPt.getLoaiPhuongTien());
        pt.setBienSo(newPt.getBienSo());
        pt.setSoChoNgoi(newPt.getSoChoNgoi());
        phuongTienService.save(pt);
        return ResponseEntity.ok("Cập nhật phương tiện thành công");
    }

    // 🗑 Xóa phương tiện
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePhuongTien(@PathVariable Integer id) {
        phuongTienService.deleteById(id);
        return ResponseEntity.ok("Xóa phương tiện thành công");
    }
}

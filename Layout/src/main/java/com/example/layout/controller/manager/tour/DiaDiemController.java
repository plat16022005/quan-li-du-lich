package com.example.layout.controller.manager.tour;

import com.example.layout.entity.DiaDiem;
import com.example.layout.service.IDiaDiemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager/diadiem")
public class DiaDiemController {

    private final IDiaDiemService diaDiemService;

    public DiaDiemController(IDiaDiemService diaDiemService) {
        this.diaDiemService = diaDiemService;
    }


    // 📌 Lấy toàn bộ danh sách địa điểm
    @GetMapping("/all")
    public List<DiaDiem> getAll() {
        return diaDiemService.findAll();
    }

    // ➕ Thêm mới địa điểm
    @PostMapping("/add")
    public DiaDiem addDiaDiem(@RequestBody DiaDiem diaDiem) {
        return diaDiemService.save(diaDiem);
    }

    // 🗑️ Xóa địa điểm
    @DeleteMapping("/{id}")
    public void deleteDiaDiem(@PathVariable Integer id) {
        diaDiemService.deleteById(id);
    }

    // ✏️ Sửa địa điểm
    @PutMapping("/{id}")
    public DiaDiem updateDiaDiem(@PathVariable Integer id, @RequestBody DiaDiem newData) {
        return diaDiemService.findById(id).map(d -> {
            d.setTenDiaDiem(newData.getTenDiaDiem());
            d.setDiaChi(newData.getDiaChi());
            return diaDiemService.save(d);
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy địa điểm với ID: " + id));
    }
}

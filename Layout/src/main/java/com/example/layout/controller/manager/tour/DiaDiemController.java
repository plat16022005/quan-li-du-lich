package com.example.layout.controller.manager.tour;

import com.example.layout.entity.DiaDiem;
import com.example.layout.repository.DiaDiemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager/diadiem")
public class DiaDiemController {

    private final DiaDiemRepository diaDiemRepository;

    public DiaDiemController(DiaDiemRepository diaDiemRepository) {
        this.diaDiemRepository = diaDiemRepository;
    }


    // 📌 Lấy toàn bộ danh sách địa điểm
    @GetMapping("/all")
    public List<DiaDiem> getAll() {
        return diaDiemRepository.findAll();
    }

    // ➕ Thêm mới địa điểm
    @PostMapping("/add")
    public DiaDiem addDiaDiem(@RequestBody DiaDiem diaDiem) {
        return diaDiemRepository.save(diaDiem);
    }

    // 🗑️ Xóa địa điểm
    @DeleteMapping("/{id}")
    public void deleteDiaDiem(@PathVariable Integer id) {
        diaDiemRepository.deleteById(id);
    }

    // ✏️ Sửa địa điểm
    @PutMapping("/{id}")
    public DiaDiem updateDiaDiem(@PathVariable Integer id, @RequestBody DiaDiem newData) {
        return diaDiemRepository.findById(id).map(d -> {
            d.setTenDiaDiem(newData.getTenDiaDiem());
            d.setDiaChi(newData.getDiaChi());
            return diaDiemRepository.save(d);
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy địa điểm với ID: " + id));
    }
}

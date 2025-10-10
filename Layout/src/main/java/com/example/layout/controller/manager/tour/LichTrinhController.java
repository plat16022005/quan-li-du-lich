package com.example.layout.controller.manager.tour;

import com.example.layout.entity.LichTrinh;
import com.example.layout.repository.LichTrinhRepository;
import com.example.layout.repository.TourRepository;
import com.example.layout.repository.DiaDiemRepository;
import com.example.layout.repository.KhachSanRepository;
import com.example.layout.repository.PhuongTienRepository;
import com.example.layout.entity.Tour;
import com.example.layout.entity.DiaDiem;
import com.example.layout.entity.KhachSan;
import com.example.layout.entity.PhuongTien;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager/lichtrinh")
public class LichTrinhController {

    @Autowired
    private LichTrinhRepository lichTrinhRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private DiaDiemRepository diaDiemRepository;

    @Autowired
    private KhachSanRepository khachSanRepository;

    @Autowired
    private PhuongTienRepository phuongTienRepository;

    @PostMapping("/add-multiple")
    public ResponseEntity<?> addMultiple(@RequestBody List<LichTrinhRequest> lichTrinhList) {
        for (LichTrinhRequest req : lichTrinhList) {
            var tour = tourRepository.findById(req.getMaTour()).orElse(null);
            if (tour == null) {
                return ResponseEntity.badRequest().body("❌ Tour không tồn tại: " + req.getMaTour());
            }

            var diaDiem = (req.getMaDiaDiem() != null)
                    ? diaDiemRepository.findById(req.getMaDiaDiem()).orElse(null)
                    : null;

            var phuongTien = (req.getMaPhuongTien() != null)
                    ? phuongTienRepository.findById(req.getMaPhuongTien()).orElse(null)
                    : null;

            var khachSan = (req.getMaKhachSan() != null)
                    ? khachSanRepository.findById(req.getMaKhachSan()).orElse(null)
                    : null;

            LichTrinh lt = new LichTrinh();
            lt.setTour(tour);
            lt.setThuTuNgay(req.getThuTuNgay());
            lt.setDiaDiem(diaDiem);
            lt.setHoatDong(req.getHoatDong());
            lt.setPhuongTien(phuongTien);
            lt.setKhachSan(khachSan);

            lichTrinhRepository.save(lt);
        }
        return ResponseEntity.ok("✅ Lưu thành công!");
    }

    // DTO nội bộ để nhận dữ liệu từ client
    public static class LichTrinhRequest {
        private Integer maTour;
        private Integer thuTuNgay;
        private Integer maDiaDiem;
        private String hoatDong;
        private Integer maPhuongTien;
        private Integer maKhachSan;

        // getters & setters
        public Integer getMaTour() { return maTour; }
        public void setMaTour(Integer maTour) { this.maTour = maTour; }
        public Integer getThuTuNgay() { return thuTuNgay; }
        public void setThuTuNgay(Integer thuTuNgay) { this.thuTuNgay = thuTuNgay; }
        public Integer getMaDiaDiem() { return maDiaDiem; }
        public void setMaDiaDiem(Integer maDiaDiem) { this.maDiaDiem = maDiaDiem; }
        public String getHoatDong() { return hoatDong; }
        public void setHoatDong(String hoatDong) { this.hoatDong = hoatDong; }
        public Integer getMaPhuongTien() { return maPhuongTien; }
        public void setMaPhuongTien(Integer maPhuongTien) { this.maPhuongTien = maPhuongTien; }
        public Integer getMaKhachSan() { return maKhachSan; }
        public void setMaKhachSan(Integer maKhachSan) { this.maKhachSan = maKhachSan; }
    }
}

package com.example.layout.controller.manager.tour;

import com.example.layout.entity.LichTrinh;
import com.example.layout.repository.LichTrinhRepository;
import com.example.layout.repository.TourRepository;
import com.example.layout.service.DiaDiemService;
import com.example.layout.service.KhachSanService;
import com.example.layout.service.LichTrinhService;
import com.example.layout.service.PhuongTienService;
import com.example.layout.service.TourService;

import jakarta.servlet.http.HttpSession;

import com.example.layout.repository.DiaDiemRepository;
import com.example.layout.repository.KhachSanRepository;
import com.example.layout.repository.PhuongTienRepository;
//import com.example.layout.entity.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manager/lichtrinh")
public class LichTrinhController {


    private final LichTrinhService lichTrinhService;
    private final TourService tourService; 
    private final DiaDiemService diaDiemService;
    private final KhachSanService khachSanService;
    private final PhuongTienService phuongTienService;

    public LichTrinhController(LichTrinhService lichTrinhService, TourService tourService, DiaDiemService diaDiemService, KhachSanService khachSanService, PhuongTienService phuongTienService) {
        this.lichTrinhService = lichTrinhService;
        this.tourService = tourService;
        this.diaDiemService = diaDiemService;
        this.khachSanService = khachSanService;
        this.phuongTienService = phuongTienService;
    }

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
    public ResponseEntity<?> addMultiple(@RequestBody List<LichTrinhRequest> lichTrinhList, HttpSession session) {
        for (LichTrinhRequest req : lichTrinhList) {
            String maTourSession = (String) session.getAttribute("matour");
            Integer maTour = Integer.parseInt(maTourSession);
            if (maTourSession == null) {
                System.out.println("Không thấy");
                return ResponseEntity.badRequest().body("❌ Không tìm thấy mã tour trong session");
            }
            var tour = tourRepository.findById(maTour).orElse(null);
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
        response.put("diaDiemList", diaDiemService.findAll());
        response.put("khachSanList", khachSanService.findAll());
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
    public ResponseEntity<LichTrinh> updateLichTrinh(@PathVariable Integer maLichTrinh, @RequestBody LichTrinh updatedData) {
        LichTrinh saved = lichTrinhService.update(maLichTrinh, updatedData); // Cần tạo hàm này trong service
        return ResponseEntity.ok(saved);
    }


    @DeleteMapping("/delete/{maLichTrinh}")
    @ResponseBody
    public ResponseEntity<?> deleteLichTrinh(@PathVariable Integer maLichTrinh) {
        lichTrinhService.deleteLichTrinh(maLichTrinh);
        return ResponseEntity.ok().build();
    }
}

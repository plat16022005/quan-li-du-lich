package com.example.layout.controller.manager.tour;

import com.example.layout.entity.LichTrinh;
import com.example.layout.repository.LichTrinhRepository;
import com.example.layout.repository.TourRepository;
import com.example.layout.service.IDiaDiemService;
import com.example.layout.service.IKhachSanService;
import com.example.layout.service.ILichTrinhService;
import com.example.layout.service.IPhuongTienService;
import com.example.layout.service.ITourService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

import com.example.layout.repository.DiaDiemRepository;
import com.example.layout.repository.KhachSanRepository;
import com.example.layout.repository.PhuongTienRepository;
//import com.example.layout.entity.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manager/lichtrinh")
public class LichTrinhController {


    private final ILichTrinhService lichTrinhService;
    private final IDiaDiemService diaDiemService;
    private final IKhachSanService khachSanService;
    private final IPhuongTienService phuongTienService;
    private final ITourService tourService; 
    
    // Sử dụng @Autowired hoặc constructor injection cho ObjectMapper
    private final ObjectMapper objectMapper;

    private final LichTrinhRepository lichTrinhRepository;
    private final TourRepository tourRepository;
    private final DiaDiemRepository diaDiemRepository;
    private final KhachSanRepository khachSanRepository;
    private final PhuongTienRepository phuongTienRepository;

    public LichTrinhController(ILichTrinhService lichTrinhService, ITourService tourService, 
                               IDiaDiemService diaDiemService, IKhachSanService khachSanService, 
                               IPhuongTienService phuongTienService, ObjectMapper objectMapper, 
                               LichTrinhRepository lichTrinhRepository, TourRepository tourRepository, 
                               DiaDiemRepository diaDiemRepository, KhachSanRepository khachSanRepository, 
                               PhuongTienRepository phuongTienRepository) {
        this.lichTrinhService = lichTrinhService;
        this.tourService = tourService;
        this.diaDiemService = diaDiemService;
        this.khachSanService = khachSanService;
        this.phuongTienService = phuongTienService;
        this.objectMapper = objectMapper;
        this.lichTrinhRepository = lichTrinhRepository;
        this.tourRepository = tourRepository;
        this.diaDiemRepository = diaDiemRepository;
        this.khachSanRepository = khachSanRepository;
        this.phuongTienRepository = phuongTienRepository;
    }


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
    	System.out.println("Vào PUT");
        LichTrinh saved = lichTrinhService.update(maLichTrinh, updatedData); // Cần tạo hàm này trong service
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

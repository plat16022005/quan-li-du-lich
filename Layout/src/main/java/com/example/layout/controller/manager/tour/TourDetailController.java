package com.example.layout.controller.manager.tour;

import com.example.layout.entity.*;
import com.example.layout.service.ILichTrinhService;
import com.example.layout.service.IDiaDiemService;
import com.example.layout.service.IKhachSanService;
import com.example.layout.service.IPhuongTienService;
import com.example.layout.service.ITourService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/manager/tour/detail")
public class TourDetailController {

    private final ILichTrinhService lichTrinhService;
    private final IDiaDiemService diaDiemService;
    private final IPhuongTienService phuongTienService;
    private final IKhachSanService khachSanService;
    private final ITourService tourService;

    public TourDetailController(
            ILichTrinhService lichTrinhService,
            IDiaDiemService diaDiemService,
            IPhuongTienService phuongTienService,
            IKhachSanService khachSanService,
            ITourService tourService
    ) {
        this.lichTrinhService = lichTrinhService;
        this.diaDiemService = diaDiemService;
        this.phuongTienService = phuongTienService;
        this.khachSanService = khachSanService;
        this.tourService = tourService;
    }

    // ✅ Lấy toàn bộ lịch trình của 1 tour
    @GetMapping("/{maTour}/lichtrinh")
    public ResponseEntity<List<LichTrinh>> getLichTrinhByTour(@PathVariable Integer maTour) {
        List<LichTrinh> lichTrinhs = lichTrinhService.getLichTrinhByTour(maTour);
        return ResponseEntity.ok(lichTrinhs);
    }

    // ✅ Thêm hoặc cập nhật lịch trình cho 1 tour
    @PostMapping("/{maTour}/lichtrinh")
    public ResponseEntity<?> saveLichTrinh(
            @PathVariable Integer maTour,
            @RequestBody List<LichTrinhRequest> lichTrinhRequests) {

        Tour tour = tourService.getTourById(maTour);
        if (tour == null) {
            throw new RuntimeException("Không tìm thấy Tour");
        }

        for (LichTrinhRequest req : lichTrinhRequests) {
            LichTrinh lt = new LichTrinh();
            lt.setTour(tour);
            lt.setThuTuNgay(req.getThuTuNgay());
            lt.setHoatDong(req.getHoatDong());

            // Địa điểm — nếu chưa có thì thêm mới
            DiaDiem diaDiem;
            if (req.getMaDiaDiem() != null) {
                diaDiem = diaDiemService.findById(req.getMaDiaDiem())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy địa điểm"));
            } else {
                DiaDiem newDD = new DiaDiem();
                newDD.setTenDiaDiem(req.getTenDiaDiem());
                newDD.setDiaChi(req.getDiaChi());
                diaDiem = diaDiemService.save(newDD);
            }
            lt.setDiaDiem(diaDiem);

            // Phương tiện
            if (req.getMaPhuongTien() != null) {
                PhuongTien pt = phuongTienService.findById(req.getMaPhuongTien())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy phương tiện"));
                lt.setPhuongTien(pt);
            }

            // Khách sạn
            if (req.getMaKhachSan() != null) {
                KhachSan ks = khachSanService.findById(req.getMaKhachSan())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy khách sạn"));
                lt.setKhachSan(ks);
            }

            lichTrinhService.saveLichTrinh(lt);
        }

        return ResponseEntity.ok("Lưu lịch trình thành công");
    }

    // 🗑 Xóa 1 lịch trình theo ID
    @DeleteMapping("/lichtrinh/{maLichTrinh}")
    public ResponseEntity<?> deleteLichTrinh(@PathVariable Integer maLichTrinh) {
        lichTrinhService.deleteLichTrinh(maLichTrinh);
        return ResponseEntity.ok("Xóa lịch trình thành công");
    }

    // 📋 Lấy danh sách phương tiện để chọn
    @GetMapping("/phuongtien/all")
    public List<PhuongTien> getAllPhuongTien() {
        return phuongTienService.findAll();
    }

    // 🏨 Lấy danh sách khách sạn để chọn
    @GetMapping("/khachsan/all")
    public List<KhachSan> getAllKhachSan() {
        return khachSanService.findAll();
    }

    // 📍 Lấy danh sách địa điểm để chọn (autocomplete)
    @GetMapping("/diadiem/all")
    public List<DiaDiem> getAllDiaDiem() {
        return diaDiemService.findAll();
    }
}

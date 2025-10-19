package com.example.layout.controller.guest;

import com.example.layout.entity.Tour;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.LichTrinh;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.DatChoRepository;
import com.example.layout.repository.LichTrinhRepository;
import com.example.layout.repository.TourRepository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GuestTourController {

    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private ChuyenDuLichRepository chuyenDuLichRepository;
    @Autowired
    private DatChoRepository datChoRepository;

    @GetMapping("home/tour/{id}")
    public String getTourDetail(@PathVariable("id") Integer id, Model model) {
        Tour tour = tourRepository.findTourWithLichTrinhs(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy Tour ID: " + id));

        tour.getDanhSachChuyen().forEach(ch -> {
            int daDat = datChoRepository.getTongSoLuongDaDat(ch.getMaChuyen());
            ch.setSoLuongHienTai(daDat);
        });

        List<LichTrinh> lichTrinhs = lichTrinhRepository.findByTour(tour);
        if (lichTrinhs == null) {
            lichTrinhs = new ArrayList<>();
        }

        // 🧠 Tạo danh sách ngày: [1, 2, 3, ... soNgay]
        List<Integer> danhSachNgay = new ArrayList<>();
        for (int i = 1; i <= tour.getSoNgay(); i++) {
            danhSachNgay.add(i);
        }

        model.addAttribute("tour", tour);
        model.addAttribute("lichTrinhs", lichTrinhs);
        model.addAttribute("danhSachNgay", danhSachNgay);

        System.out.println("=== Mapping 1 ===");
        return "guest/tour_detail";
    }


    
    @Autowired
    private LichTrinhRepository lichTrinhRepository;

    @GetMapping("/home/tour/{id}/list")
    public String getChuyenDetail(@PathVariable("id") Integer id, Model model) {

        // Lấy thông tin Tour
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Tour ID: " + id));

        // Lấy danh sách chuyến của tour này
        List<ChuyenDuLich> danhSachChuyen = chuyenDuLichRepository.findByTour(tour);

        // Tính số lượng vé đã đặt cho từng chuyến
        danhSachChuyen.forEach(ch -> {
            int daDat = datChoRepository.getTongSoLuongDaDat(ch.getMaChuyen());
            ch.setSoLuongHienTai(daDat);
        });

        model.addAttribute("tour", tour);
        model.addAttribute("danhSachChuyen", danhSachChuyen);

        return "guest/listchuyen"; // file giao diện bạn sẽ tạo
    }



}

package com.example.layout.controller;

import com.example.layout.dto.TourDetailDTO;
import com.example.layout.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tours")
public class TourController {

    @Autowired
    private TourService tourService;

    @GetMapping("/{id}")
    public String showTourDetails(@PathVariable("id") Integer tourId, Model model) {
        try {
            TourDetailDTO tourDetails = tourService.getTourDetails(tourId);
            model.addAttribute("tourDetails", tourDetails);
            return "trip-details"; // Tên của file HTML chi tiết
        } catch (RuntimeException e) {
            // Xử lý trường hợp không tìm thấy tour
            return "redirect:/"; // Hoặc trang lỗi 404
        }
    }
}
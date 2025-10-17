package com.example.layout.controller.guest;

import com.example.layout.entity.Tour;
import com.example.layout.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GuestTourController {

    @Autowired
    private TourRepository tourRepository;

    @GetMapping("home/tour/{id}")
    public String getTourDetail(@PathVariable("id") Integer id, Model model) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Tour ID: " + id));

        model.addAttribute("tour", tour);
        return "guest/tour_detail"; // 👈 file .html mà bạn đã thiết kế
    }
}

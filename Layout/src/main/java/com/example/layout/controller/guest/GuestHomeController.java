package com.example.layout.controller.guest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.layout.entity.User;
import com.example.layout.repository.TourRepository;
import com.example.layout.entity.Tour;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class GuestHomeController {
	@Autowired
	private TourRepository tourRepository;
	@GetMapping("/home")
    public String showHomeForm(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		if (user == null)
		{
			return "redirect:/access_denied";
		}
		if (user.getMaVaiTro() != 4)
		{
			return "redirect:/access_denied";
		}
        List<Tour> tours = tourRepository.findAll();
        model.addAttribute("tours", tours);
        return "guest/home";
    }
	
	@GetMapping("/home/tour")
	public String listTours(
	        @RequestParam(value = "destination", required = false) String destination,
	        @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
	        @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
	        @RequestParam(value = "startDate", required = false) LocalDate startDate,
	        @RequestParam(value = "endDate", required = false) LocalDate endDate,
	        @RequestParam(value = "page", defaultValue = "0") int page,
	        Model model
	) {
	    Pageable pageable = PageRequest.of(page, 6);

	    Page<Tour> tours = tourRepository.findToursByFilters(
	            (destination != null && !destination.isEmpty()) ? destination : null,
	            minPrice,
	            maxPrice,
	            startDate,
	            endDate,
	            pageable
	    );

	    model.addAttribute("tours", tours.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", tours.getTotalPages());
	    model.addAttribute("destination", destination);
	    model.addAttribute("minPrice", minPrice);
	    model.addAttribute("maxPrice", maxPrice);
	    model.addAttribute("startDate", startDate);
	    model.addAttribute("endDate", endDate);

	    return "guest/all_tour";
	}

}

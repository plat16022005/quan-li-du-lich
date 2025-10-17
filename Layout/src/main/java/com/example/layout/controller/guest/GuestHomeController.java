package com.example.layout.controller.guest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}

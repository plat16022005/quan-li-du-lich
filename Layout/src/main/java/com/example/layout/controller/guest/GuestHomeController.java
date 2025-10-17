package com.example.layout.controller.guest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.layout.entity.User;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class GuestHomeController {
	@GetMapping("/home")
    public String showHomeForm(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null)
		{
			return "redirect:/access_denied";
		}
		if (user.getMaVaiTro() != 4)
		{
			return "redirect:/access_denied";
		}
        return "guest/welcome";
    }
}

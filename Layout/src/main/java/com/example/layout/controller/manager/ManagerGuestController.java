package com.example.layout.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.layout.entity.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class ManagerGuestController {
	@GetMapping("/manager/guest")
    public String showGuestForm(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null)
		{
			return "redirect:/access-denied";
		}
		if (user.getMaVaiTro() != 1)
		{
			return "redirect:/access-denied";
		}
        return "manager/guest";
    }
}

package com.example.layout.controller.manager;

import jakarta.servlet.http.HttpSession;

import com.example.layout.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManagerHomeController {
	@GetMapping("/manager/home")
    public String showHomeForm(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null)
		{
			return "redirect:/access-denied";
		}
		if (user.getMaVaiTro() != 1)
		{
			return "redirect:/access-denied";
		}
        return "manager/home";
    }
}

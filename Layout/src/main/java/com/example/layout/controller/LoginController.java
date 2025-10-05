package com.example.layout.controller;

import com.example.layout.entity.User;
import com.example.layout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/do-login")
    public String handleLogin(@RequestParam String username,
                              @RequestParam String password,
                              Model model) {
    	System.out.println("Vô post");
        User user = userService.login(username, password);

        if (user != null) {
        	System.out.println("Đăng nhập thành công");
            model.addAttribute("username", user.getTenDangNhap());
            return "welcome";
        } else {
            model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu");
            return "login";
        }
    }
}

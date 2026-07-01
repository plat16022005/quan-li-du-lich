package com.example.layout.config;

import com.example.layout.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("user")
    public User globalUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            return (User) auth.getPrincipal();
        }
        return null;
    }
}


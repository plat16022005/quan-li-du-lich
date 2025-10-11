package com.example.layout.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.layout.entity.User;
import com.example.layout.service.EmailService;
import com.example.layout.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
   
    
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/do-login")
    public String handleLogin(@RequestParam String username,
                              @RequestParam String password,
  							RedirectAttributes redirectAttributes,
  							HttpSession session) {
        User user = userService.login(username, password);
        
        if (user != null && user.getTrangThai() == true) {
        	session.setAttribute("user", user);
        	if (user.getMaVaiTro() == 1)
        		return "redirect:/manager/home";
        	else
        		return "redirect:/home";
        } else {
        	redirectAttributes.addFlashAttribute("error", "Sai tên đăng nhập hoặc mật khẩu");
            return "redirect:/login";
        }
    }
    
    @GetMapping("/register")
    public String showRegisterForm()
    {
    	return "register";
    }
    
    @PostMapping("do-register")
    public String handleRegister(@RequestParam String username,
					            @RequestParam String password,
					            @RequestParam String rePassword,
					            @RequestParam String hoVaTen,
					            @RequestParam String email,
					            @RequestParam String soDienThoai,
    							RedirectAttributes redirectAttributes)
    {
    	
    	if (!password.equals(rePassword))
    	{
    		redirectAttributes.addFlashAttribute("error", "Vui lòng nhập lại đúng mật khẩu");
    		return "redirect:/register";
    	}
    	Boolean user = userService.register(username, password, hoVaTen, email, soDienThoai);
    	if (user != false)
    	{
    		redirectAttributes.addFlashAttribute("success", "Đăng kí tài khoản thành công! Vui lòng đăng nhập lại!");
    		return "redirect:/login";
    	}
    	else
    	{
    		redirectAttributes.addFlashAttribute("error", "Tài khoản này đã có người đăng kí");
    		return "redirect:/register";
    	}
    }
    
    @GetMapping("/forgot-pass")
    public String showForgotPasswordForm()
    {
    	return "forgot_password";
    }
    
    @PostMapping("/get-confirm")
    public String handleForgot(@RequestParam String email,
    							RedirectAttributes redirectAttributes,
    							HttpSession session)
    {
    	User user = userService.getConfirm(email);
    	if (user != null)
    	{
    	    String otp = emailService.generateOTP(6);
    	    session.setAttribute("otp", otp);
    	    session.setAttribute("email", email);
    	    session.setMaxInactiveInterval(300); // OTP hết hạn sau 5 phút

    	    emailService.sendEmail(email, "Mã xác nhận TravelGO", "Mã OTP của bạn là: " + otp);
    	    redirectAttributes.addFlashAttribute("success", "Mã OTP đã được gửi tới email của bạn!");
    		return "redirect:/confirm-otp";
    	}
    	else
    	{
    		redirectAttributes.addFlashAttribute("error", "Email không tồn tại! Vui lòng nhập email lúc bạn đăng kí tài khoản");
    		return "redirect:/forgot-pass";
    	}
    }
    
    @GetMapping("/confirm-otp")
    public String showConfirmOtpForm()
    {
    	return "confirm_pass";
    }
    
    @PostMapping("/do-confirm-otp")
    public String handleConfirmotp(@RequestParam("otp") List<String> otpDigits,
									RedirectAttributes redirectAttributes,
									HttpSession session)
    {
    	String otp = String.join("", otpDigits);
    	String OTP = (String) session.getAttribute("otp");
    	if (OTP.equals(otp))
    	{
    		return "redirect:/reset-pass";
    	}
    	else
    	{
    		redirectAttributes.addFlashAttribute("error", "OTP không đúng!");
    		return "redirect:/confirm-otp";
    	}
    }
    @GetMapping("/reset-pass")
    public String showResetPasswordForm()
    {
    	return "reset_password";
    }
    
    @PostMapping("/do-reset-pass")
    public String handleResetPass(@RequestParam String password,
    							@RequestParam String rePassword,
    							RedirectAttributes redirectAttributes,
    							HttpSession session)
    {
    	String email = (String) session.getAttribute("email");
    	if (password.equals(rePassword))
    	{
    		userService.resetpass(email, password);
    		return "redirect:/login";
    	}
    	else
    	{
    		redirectAttributes.addFlashAttribute("error", "Vui lòng nhập lại đúng mật khẩu!");
    		return "redirect:/reset-pass";
    	}
    }
    @GetMapping("/access-denied")
    public String showAccessDeniedForm()
    {
    	return "access-denied";
    }
    @GetMapping("/logout")
    public String handleLogout(HttpSession session,
    						RedirectAttributes redirectAttributes)
    {
    	session.invalidate();
    	return "redirect:/login";
    }
}

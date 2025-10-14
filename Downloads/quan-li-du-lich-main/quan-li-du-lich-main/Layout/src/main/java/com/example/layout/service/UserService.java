package com.example.layout.service;

import com.example.layout.entity.User;
import com.example.layout.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service // 👈 BẮT BUỘC CÓ DÒNG NÀY
public class UserService {

	@Autowired
    private UserRepository userRepository;
    
    public User login(String username, String password) {
        User user = userRepository.findByTenDangNhap(username);
        if (user != null && user.getMatKhau().equals(password)) {
            return user;
        }
        return null;
    }
    public User getConfirm(String email)
    {
    	User user = userRepository.findByEmail(email);
        if (user != null) {
            return user;
        }
        return null;
    }
    
    public boolean register(String username, String password, String hoten, String email, String sodienthoai)
    {
    	User tonTai = userRepository.findByTenDangNhap(username);
    	if (tonTai != null)
    	{
    		return false;
    	}
    	User user = new User();
    	user.setTenDangNhap(username);
    	user.setMatKhau(password);
    	user.setHoTen(hoten);
    	user.setEmail(email);
    	user.setSoDienThoai(sodienthoai);
    	user.setMaVaiTro(4);
    	user.setTrangThai(true);
    	userRepository.save(user);
    	return true;
    }
    public void resetpass(String email, String password)
    {
    	User user = userRepository.findByEmail(email);
    	user.setMatKhau(password);
    	userRepository.save(user);
    }
}

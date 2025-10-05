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
}

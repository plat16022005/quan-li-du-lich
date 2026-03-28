package com.example.layout.service;

import com.example.layout.entity.User;
import com.example.layout.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User login(String username, String password) {
        User user = userRepository.findByTenDangNhap(username);
        if (user != null && user.getMatKhau().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public User getConfirm(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean register(String username, String password, String hoten, String email, String sodienthoai) {
        User tonTai = userRepository.findByTenDangNhap(username);
        if (tonTai != null) {
            return false;
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email " + email + " đã tồn tại!");
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

    @Override
    public void resetpass(String email, String password) {
        User user = userRepository.findByEmail(email);
        user.setMatKhau(password);
        userRepository.save(user);
    }
}

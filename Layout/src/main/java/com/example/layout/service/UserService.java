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

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void updateProfile(Integer userId, String hoTen, String email, String soDienThoai) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        existingUser.setHoTen(hoTen);
        existingUser.setEmail(email);
        existingUser.setSoDienThoai(soDienThoai);
        userRepository.save(existingUser);
    }

    @Override
    public void changePassword(Integer userId, String currentPassword, String newPassword) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        if (!existingUser.getMatKhau().equals(currentPassword)) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng!");
        }

        if (newPassword.length() < 6) {
            throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự!");
        }

        existingUser.setMatKhau(newPassword);
        userRepository.save(existingUser);
    }
}

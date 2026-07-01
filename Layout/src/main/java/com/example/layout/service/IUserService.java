package com.example.layout.service;

import com.example.layout.entity.User;

public interface IUserService {
    User login(String username, String password);
    User getConfirm(String email);
    boolean register(String username, String password, String hoten, String email, String sodienthoai);
    void resetpass(String email, String password);
    User findById(Integer id);
    User save(User user);
    void updateProfile(Integer userId, String hoTen, String email, String soDienThoai);
    void changePassword(Integer userId, String currentPassword, String newPassword);
}

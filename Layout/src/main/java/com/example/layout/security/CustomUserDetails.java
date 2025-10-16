package com.example.layout.security;

import com.example.layout.entity.Nhanvien; 
import com.example.layout.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User userAccount;
    private final Nhanvien employeeInfo;

    public CustomUserDetails(User user, Nhanvien nhanVien) { 
        this.userAccount = user;
        this.employeeInfo = nhanVien;
    }

    // === CÁC PHƯƠNG THỨC TIỆN ÍCH ===
    public int getMaNhanVien() {
        return this.employeeInfo.getMaNhanVien();
    }
    
    public int getMaVaiTro() {
        return this.userAccount.getMaVaiTro();
    }

    public String getHoTen() {
        return this.userAccount.getHoTen();
    }
    
    public String getChucVu() {
        return this.employeeInfo.getChucVu();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = null;
        Integer maVaiTro = this.userAccount.getMaVaiTro();

        // Chuyển đổi từ mã vai trò (số) sang tên vai trò (chữ)
        if (maVaiTro == 3) {
            roleName = "Hướng dẫn viên"; 
        } else if (maVaiTro == 5) {
            roleName = "Tài xế";
        }
        return Collections.singleton(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return userAccount.getMatKhau();
    }

    @Override
    public String getUsername() {
        return userAccount.getTenDangNhap();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return userAccount.getTrangThai();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userAccount.getTrangThai();
    }
}
package com.example.layout.security;

import com.example.layout.entity.Nhanvien; 
import com.example.layout.entity.User;
import com.example.layout.repository.NhanvienRepository;
import com.example.layout.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NhanvienRepository nhanvienRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	
    	Optional<User> userOptional = userRepository.findOptionalByTenDangNhap(username);
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));

        Optional<Nhanvien> employeeList = nhanvienRepository.findByTaiKhoan_MaTaiKhoan(user.getMaTaiKhoan());
        
        if (employeeList.isEmpty()) {
            throw new UsernameNotFoundException("Tài khoản này không phải là nhân viên: " + username);
        }
        
        Nhanvien nhanVien = employeeList.get();

        return new CustomUserDetails(user, nhanVien);
    }
}
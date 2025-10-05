package com.example.layout.repository;

import com.example.layout.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByTenDangNhap(String tenDangNhap);
}

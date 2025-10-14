package com.example.layout.repository;

import com.example.layout.entity.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
	User findByTenDangNhap(String tenDangNhap);
	User findByEmail(String email);
	boolean existsByTenDangNhap(String tenDangNhap);
	List<User> findByTrangThaiTrue();
}

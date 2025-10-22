package com.example.layout.repository;

import com.example.layout.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	User findByTenDangNhap(String tenDangNhap);
	User findByEmail(String email);
	boolean existsByTenDangNhap(String tenDangNhap);
	List<User> findByTrangThaiTrue();
	Optional<User> findOptionalByTenDangNhap(String tenDangNhap);
	boolean existsByEmail(String email);
}

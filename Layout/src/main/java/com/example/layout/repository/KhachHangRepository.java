package com.example.layout.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.layout.entity.KhachHang;

public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
}

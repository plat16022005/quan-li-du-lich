package com.example.layout.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.layout.entity.Nhanvien;

@Repository
public interface NhanvienRepository extends JpaRepository<Nhanvien, Long> {

}
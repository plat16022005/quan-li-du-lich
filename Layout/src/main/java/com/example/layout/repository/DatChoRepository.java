package com.example.layout.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.layout.entity.DatCho;

@Repository
public interface DatChoRepository extends JpaRepository<DatCho, Integer> {
    List<DatCho> findByKhachHang_MaKhachHang(Integer maKhachHang);
}
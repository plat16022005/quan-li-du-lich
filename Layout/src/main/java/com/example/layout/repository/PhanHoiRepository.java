package com.example.layout.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.example.layout.entity.PhanHoi;

@Repository
public interface PhanHoiRepository extends JpaRepository<PhanHoi, Long> {
	List<PhanHoi> findByKhachHangMaKhachHang(int maKhachHang); 
}
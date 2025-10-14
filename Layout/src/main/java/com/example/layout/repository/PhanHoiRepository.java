package com.example.layout.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.example.layout.entity.PhanHoi;

public interface PhanHoiRepository extends JpaRepository<PhanHoi, Integer> {
	List<PhanHoi> findByKhachHangMaKhachHang(Integer maKhachHang);
}
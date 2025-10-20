package com.example.layout.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.example.layout.entity.PhanHoi;

@Repository
public interface PhanHoiRepository extends JpaRepository<PhanHoi, Long> {
	List<PhanHoi> findByKhachHangMaKhachHang(int maKhachHang);
    @Query("SELECT p.chuyenDuLich.maChuyen FROM PhanHoi p WHERE p.khachHang.maKhachHang = :id")
    List<Integer> findMaChuyenDaDanhGia(@Param("id") Integer id);
    List<PhanHoi> findByChuyenDuLich_Tour_MaTour(Integer maTour);    
}

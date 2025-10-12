package com.example.layout.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.layout.entity.KhachHang;

public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
	KhachHang findByTaiKhoan_MaTaiKhoan(Integer maTaiKhoan);
	@Query("SELECT COALESCE(k.bietDen, 'other'), COUNT(k) FROM KhachHang k GROUP BY COALESCE(k.bietDen, 'other')")
	List<Object[]> thongKeNguonKhachHang();
    @Query("SELECT COUNT(k) FROM KhachHang k WHERE MONTH(k.ngayThamGia) = MONTH(:today) AND YEAR(k.ngayThamGia) = YEAR(:today)")
    long countKhachHangMoi(LocalDate today);
}

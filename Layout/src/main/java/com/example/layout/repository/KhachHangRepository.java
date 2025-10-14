package com.example.layout.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.layout.entity.KhachHang;

@Repository	
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
	KhachHang findByTaiKhoan_MaTaiKhoan(Integer maTaiKhoan);
	
	@Query("SELECT COALESCE(k.bietDen, 'other'), COUNT(k) FROM KhachHang k GROUP BY COALESCE(k.bietDen, 'other')")
	List<Object[]> thongKeNguonKhachHang();
    
	@Query("SELECT COUNT(k) FROM KhachHang k WHERE MONTH(k.ngayThamGia) = MONTH(:today) AND YEAR(k.ngayThamGia) = YEAR(:today)")
    long countKhachHangMoi(LocalDate today);

	@Query("""
        SELECT kh FROM KhachHang kh
        WHERE LOWER(kh.taiKhoan.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(kh.taiKhoan.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<KhachHang> search(@Param("keyword") String keyword);
}

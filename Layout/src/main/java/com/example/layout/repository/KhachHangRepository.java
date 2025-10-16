package com.example.layout.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.layout.dto.HanhKhachDTO;
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
	
	@Query("""
	        SELECT kh FROM KhachHang kh
	        WHERE :keyword IS NULL OR (
	            LOWER(kh.taiKhoan.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
	            LOWER(kh.taiKhoan.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
	        )
	    """)
	Page<KhachHang> search(@Param("keyword") String keyword, Pageable pageable);
	
	@Query("SELECT new com.example.layout.dto.HanhKhachDTO(tk.hoTen, tk.soDienThoai, tk.email, kh.gioiTinh) " +
	           "FROM DatCho dc " +
	           "JOIN dc.khachHang kh " +
	           "JOIN kh.taiKhoan tk " +
	           "WHERE dc.chuyenDuLich.maChuyen = :maChuyen " +
	           "AND (dc.trangThai = 'Đã thanh toán' OR dc.trangThai = 'Đã xác nhận')")
	    List<HanhKhachDTO> findHanhKhachByMaChuyen(@Param("maChuyen") Integer maChuyen);
	
}

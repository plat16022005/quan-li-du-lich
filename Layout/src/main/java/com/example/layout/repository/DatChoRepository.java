package com.example.layout.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.layout.entity.DatCho;

@Repository
public interface DatChoRepository extends JpaRepository<DatCho, Integer> {
    List<DatCho> findByKhachHang_MaKhachHang(Integer maKhachHang);
    long countByTrangThai(String trangThai);
    
    @Query("""
            SELECT d FROM DatCho d
            WHERE (:keyword IS NULL OR LOWER(d.khachHang.taiKhoan.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:status IS NULL OR d.trangThai = :status)
        """)
    Page<DatCho> searchAndFilter(@Param("keyword") String keyword, @Param("status") String status, Pageable pageable);
    @Query("SELECT dc FROM DatCho dc JOIN dc.chuyenDuLich c WHERE c.tour.maTour = :maTour")
    List<DatCho> findByTourMaTour(@Param("maTour") Integer maTour);
}

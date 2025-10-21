package com.example.layout.repository;

import com.example.layout.entity.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer> {

    @Query("SELECT km FROM KhuyenMai km WHERE LOWER(km.tenKM) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(km.maCode) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<KhuyenMai> searchByKeyword(String keyword);
    
    @Query(value = "SELECT COALESCE(TiLeGiam, 0) FROM KhuyenMai WHERE MaCode = :maCode", nativeQuery = true)
    BigDecimal findTiLeGiamByMaCode(@Param("maCode") String maCode);
    
    @Query("""
            SELECT k FROM KhuyenMai k
            WHERE CURRENT_DATE BETWEEN k.ngayBatDau AND k.ngayKetThuc
            ORDER BY k.ngayKetThuc ASC
        """)
        List<KhuyenMai> findKhuyenMaiDangHieuLuc();
}

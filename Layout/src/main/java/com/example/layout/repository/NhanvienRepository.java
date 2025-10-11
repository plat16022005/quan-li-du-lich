package com.example.layout.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.layout.entity.Nhanvien;

@Repository
public interface NhanvienRepository extends JpaRepository<Nhanvien, Integer> {
    @Query("""
            SELECT nv FROM Nhanvien nv
            WHERE (:keyword IS NULL OR LOWER(nv.taiKhoan.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:role IS NULL OR nv.taiKhoan.maVaiTro = :role)
            AND (:status IS NULL OR nv.taiKhoan.trangThai = :status)
        """)
        Page<Nhanvien> searchStaff(
                @Param("keyword") String keyword,
                @Param("role") Integer role,
                @Param("status") Boolean status,
                Pageable pageable
        );
}
package com.example.layout.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.layout.entity.Nhanvien;
import com.example.layout.entity.Vaitro;

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

        List<Nhanvien> findByTaiKhoan_MaVaiTro(Integer maVaiTro);

        @Query("""
        SELECT nv FROM Nhanvien nv
        WHERE nv.taiKhoan.maVaiTro = :role
        AND nv.id NOT IN (
                SELECT c.huongDanVien.id FROM ChuyenDuLich c
                WHERE c.huongDanVien IS NOT NULL
                AND c.ngayKetThuc > :startDate AND c.ngayBatDau < :endDate
        )
        AND nv.id NOT IN (
                SELECT c.taiXe.id FROM ChuyenDuLich c
                WHERE c.taiXe IS NOT NULL
                AND c.ngayKetThuc > :startDate AND c.ngayBatDau < :endDate
        )
        """)
        List<Nhanvien> findAvailableStaff(
                @Param("role") Vaitro role,
                @Param("startDate") LocalDate startDate,
                @Param("endDate") LocalDate endDate
        );
}

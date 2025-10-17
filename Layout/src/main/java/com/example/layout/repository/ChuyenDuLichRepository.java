package com.example.layout.repository;

import com.example.layout.entity.ChuyenDuLich;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
//import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChuyenDuLichRepository extends JpaRepository<ChuyenDuLich, Integer> {

       Optional<ChuyenDuLich> findTopByTour_MaTourOrderByNgayBatDauAsc(Integer maTour);

       @Query("SELECT COALESCE(SUM(ct.soLuong), 0) " +
              "FROM ChiTietDatCho ct " +
              "JOIN ct.datCho dc " +
              "JOIN dc.chuyenDuLich cd " +
              "WHERE cd.maChuyen = :maChuyen")
       int getTotalParticipants(@Param("maChuyen") Integer maChuyen);
       @Query("SELECT COUNT(c) FROM ChuyenDuLich c WHERE c.trangThai = 'Đang diễn ra'")
       long countChuyenDangDienRa();
       @Query("SELECT c FROM ChuyenDuLich c " +
              "WHERE c.trangThai = 'Sắp diễn ra' " +
              "ORDER BY c.ngayBatDau ASC")
       List<ChuyenDuLich> findChuyenSapKhoiHanh();
       List<ChuyenDuLich> findByTrangThai(String trangThai);
       long countByTrangThai(String trangThai);
       

       List<ChuyenDuLich> findByHuongDanVienIsNullAndTrangThai(String trangThai);

       List<ChuyenDuLich> findByTaiXeIsNullAndTrangThai(String trangThai);

       List<ChuyenDuLich> findByHuongDanVien_MaNhanVien(Integer maNhanVien);

       List<ChuyenDuLich> findByTaiXe_MaNhanVien(Integer maNhanVien);

      
       @Query("SELECT c FROM ChuyenDuLich c " +
               "WHERE MONTH(c.ngayBatDau) = :month AND YEAR(c.ngayBatDau) = :year " +
               "AND (c.huongDanVien.maNhanVien = :staffId OR c.taiXe.maNhanVien = :staffId)")
        List<ChuyenDuLich> findByMonthYearAndStaff(@Param("month") int month,
                                                   @Param("year") int year,
                                                   @Param("staffId") Integer staffId);


        @Query("SELECT c FROM ChuyenDuLich c " +
               "WHERE c.ngayBatDau BETWEEN :fromDate AND :toDate " +
               "AND (c.huongDanVien.maNhanVien = :staffId OR c.taiXe.maNhanVien = :staffId)")
        List<ChuyenDuLich> findByPeriodAndStaff(@Param("fromDate") LocalDate fromDate,
                                               @Param("toDate") LocalDate toDate,
                                               @Param("staffId") Integer staffId);


        @Query("SELECT c FROM ChuyenDuLich c " +
               "WHERE YEAR(c.ngayBatDau) = :year " +
               "AND (c.huongDanVien.maNhanVien = :staffId OR c.taiXe.maNhanVien = :staffId)")
        List<ChuyenDuLich> findByYearAndStaff(@Param("year") int year,
                                             @Param("staffId") Integer staffId);
        
        @Query("""
                SELECT c FROM ChuyenDuLich c
                WHERE c.trangThai = 'Đã kết thúc'
                AND (c.huongDanVien.maNhanVien = :maNhanVien OR c.taiXe.maNhanVien = :maNhanVien)
                AND c.ngayKetThuc BETWEEN :startDate AND :endDate
            """)
            List<ChuyenDuLich> findCompletedTripsByEmployeeAndDateRange(
                @Param("maNhanVien") Integer maNhanVien,
                @Param("startDate") LocalDate startDate,
                @Param("endDate") LocalDate endDate
            );
}

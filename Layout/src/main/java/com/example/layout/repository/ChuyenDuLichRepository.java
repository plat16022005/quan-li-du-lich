package com.example.layout.repository;

import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.LichTrinh;
import com.example.layout.entity.Tour;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.*;
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

       List<ChuyenDuLich> findByTour_MaTour(Integer maTour);

       Page<ChuyenDuLich> findByTour_MaTour(Integer maTour, Pageable pageable);

      
       @Query("SELECT c FROM ChuyenDuLich c " +
    	       "WHERE ((MONTH(c.ngayKetThuc) = :month AND YEAR(c.ngayKetThuc) = :year) " +
    	       "OR (MONTH(c.ngayBatDau) = :month AND YEAR(c.ngayBatDau) = :year)) " +
    	       "AND ((c.huongDanVien IS NOT NULL AND c.huongDanVien.maNhanVien = :staffId) " +
    	       "OR (c.taiXe IS NOT NULL AND c.taiXe.maNhanVien = :staffId)) " +
    	       "ORDER BY c.ngayBatDau DESC")
    	List<ChuyenDuLich> findByMonthYearAndStaff(@Param("month") int month, 
    	                                             @Param("year") int year, 
    	                                             @Param("staffId") Integer staffId);

    	@Query("SELECT c FROM ChuyenDuLich c " +
    	       "WHERE c.ngayKetThuc BETWEEN :fromDate AND :toDate " +
    	       "AND ((c.huongDanVien IS NOT NULL AND c.huongDanVien.maNhanVien = :staffId) " +
    	       "OR (c.taiXe IS NOT NULL AND c.taiXe.maNhanVien = :staffId)) " +
    	       "ORDER BY c.ngayBatDau DESC")
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

       List<ChuyenDuLich> findByTourAndTrangThai(Tour tour, String trangThai);

       List<ChuyenDuLich> findByTourAndNgayBatDauAfter(Tour tour, LocalDate date);
       
       @Query("SELECT COUNT(c) FROM ChuyenDuLich c WHERE c.ngayBatDau BETWEEN :startDate AND :endDate AND c.trangThai = :trangThai")
       Long countByNgayBatDauBetweenAndTrangThai(@Param("startDate") java.time.LocalDate startDate, 
                                                 @Param("endDate") java.time.LocalDate endDate, 
                                                 @Param("trangThai") String trangThai);
       
       @Query("""
    		    SELECT c FROM ChuyenDuLich c
    		    WHERE c.maChuyen <> :excludeTripId
    		    AND c.trangThai IN ('Sắp diễn ra', 'Đang diễn ra')
    		    AND (
    		        (c.huongDanVien.maNhanVien = :maNhanVien AND :isHDV = true)
    		        OR (c.taiXe.maNhanVien = :maNhanVien AND :isHDV = false)
    		    )
    		    AND NOT (c.ngayKetThuc < :startDate OR c.ngayBatDau > :endDate)
    		""")
    		List<ChuyenDuLich> findConflictingTrips(
    		    @Param("excludeTripId") Integer excludeTripId,
    		    @Param("maNhanVien") Integer maNhanVien,
    		    @Param("isHDV") boolean isHDV,
    		    @Param("startDate") LocalDate startDate,
    		    @Param("endDate") LocalDate endDate
    		);
       
       @Query("""
    	        SELECT c FROM ChuyenDuLich c
    	        LEFT JOIN c.tour t
    	        WHERE (c.huongDanVien.maNhanVien = :staffId OR c.taiXe.maNhanVien = :staffId)
    	        AND (COALESCE(:trangThai, '') = '' OR c.trangThai = :trangThai)
    	        AND (COALESCE(:tenTour, '') = '' OR t.tenTour IS NULL OR LOWER(t.tenTour) LIKE LOWER(CONCAT('%', :tenTour, '%')))
    	        AND (:tuNgay IS NULL OR c.ngayBatDau >= :tuNgay)
    	        AND (:denNgay IS NULL OR c.ngayKetThuc <= :denNgay)
    	        ORDER BY c.maChuyen DESC
    	    """)
    	    List<ChuyenDuLich> searchAssignedTrips(
    	        @Param("staffId") Integer staffId,
    	        @Param("trangThai") String trangThai,
    	        @Param("tenTour") String tenTour,
    	        @Param("tuNgay") LocalDate tuNgay,
    	        @Param("denNgay") LocalDate denNgay
    	    );

    	    @Query("""
    	        SELECT c FROM ChuyenDuLich c
    	        LEFT JOIN c.tour t
    	        WHERE ((c.huongDanVien IS NULL AND :role = 3) OR (c.taiXe IS NULL AND :role = 5))
    	        AND c.trangThai = 'Sắp diễn ra'
    	        AND (COALESCE(:tenTour, '') = '' OR t.tenTour IS NULL OR LOWER(t.tenTour) LIKE LOWER(CONCAT('%', :tenTour, '%')))
    	        AND (:tuNgay IS NULL OR c.ngayBatDau >= :tuNgay)
    	        AND (:denNgay IS NULL OR c.ngayKetThuc <= :denNgay)
    	        ORDER BY c.maChuyen DESC
    	    """)
    	    List<ChuyenDuLich> searchAvailableTrips(
    	        @Param("role") Integer role,
    	        @Param("tenTour") String tenTour,
    	        @Param("tuNgay") LocalDate tuNgay,
    	        @Param("denNgay") LocalDate denNgay
    	    );

    	    @Query("SELECT c FROM ChuyenDuLich c " +
    	    	       "WHERE c.ngayKetThuc < :today " +
    	    	       "AND c.trangThai = 'Đang diễn ra'")
    	    	List<ChuyenDuLich> findTripsToComplete(@Param("today") LocalDate today);

    	    	@Query("SELECT c FROM ChuyenDuLich c " +
    	    	       "WHERE c.ngayBatDau = :today " +
    	    	       "AND c.trangThai = 'Sắp diễn ra'")
    	    	List<ChuyenDuLich> findTripsToStart(@Param("today") LocalDate today);


    	    	@Query("SELECT c FROM ChuyenDuLich c " +
    	    		       "WHERE c.ngayBatDau BETWEEN :startDate AND :endDate " +
    	    		       "AND c.trangThai = 'Sắp diễn ra' " +
    	    		       "AND (c.huongDanVien.maNhanVien = :staffId OR c.taiXe.maNhanVien = :staffId) " +
    	    		       "ORDER BY c.ngayBatDau ASC")
    	    		List<ChuyenDuLich> findUpcomingTripsByStaff(
    	    		    @Param("startDate") LocalDate startDate,
    	    		    @Param("endDate") LocalDate endDate,
    	    		    @Param("staffId") Integer staffId
    	    		);
        List<ChuyenDuLich> findByTour(Tour tour);
        @Query("""
        	    SELECT c 
        	    FROM ChuyenDuLich c
        	    WHERE c.tour = :tour
        	      AND c.trangThai = 'Sắp diễn ra'
        	""")
        	List<ChuyenDuLich> findUpcomingTripsByTour(@Param("tour") Tour tour);

        @Query("""
        	    SELECT c FROM DatCho d
        	    JOIN d.chuyenDuLich c
        	    JOIN c.tour t
        	    WHERE d.khachHang.maKhachHang = :maKH
        	    AND c.trangThai = 'Đã hoàn thành'
        	    AND d.trangThai = 'Đã thanh toán'
        	""")
        	List<ChuyenDuLich> findChuyenDaHoanThanh(@Param("maKH") Integer maKH);

//        @Query("""
//        	    SELECT DISTINCT c FROM DatCho d
//        	    JOIN d.chuyenDuLich c
//        	    JOIN c.tour t
//        	    WHERE d.khachHang.maKhachHang = :maKH
//        	    AND (c.trangThai = 'Sắp diễn ra' OR c.trangThai = 'Đang diễn ra')
//        	    AND d.trangThai = 'Đã thanh toán'
//        	""")
//        	List<ChuyenDuLich> findChuyenSapDienRa(@Param("maKH") Integer maKH);


        	@Query("""
        		    SELECT c FROM DatCho d
        		    JOIN d.chuyenDuLich c
        		    WHERE d.maDatCho = :maDatCho
        		""")
        		ChuyenDuLich findChuyenByMaDatCho(@Param("maDatCho") Integer maDatCho);
        	@Query("""
        		    SELECT d.maDatCho FROM DatCho d
        		    WHERE d.khachHang.maKhachHang = :maKH
        		    AND d.chuyenDuLich.maChuyen = :maChuyen
        		""")
        		Integer findMaDatChoByKhachHangAndChuyen(
        		        @Param("maKH") Integer maKH,
        		        @Param("maChuyen") Integer maChuyen
        		);
        	@Query("""
            	    SELECT c FROM DatCho d
            	    JOIN d.chuyenDuLich c
            	    JOIN c.tour t
            	    WHERE d.khachHang.maKhachHang = :maKH
            	    AND c.trangThai = 'Sắp diễn ra'
            	    AND d.trangThai = 'Chưa thanh toán'
            	""")
            	List<ChuyenDuLich> findChuyenSapDienRaChuaThanhToan(@Param("maKH") Integer maKH);        	
}

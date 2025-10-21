package com.example.layout.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.layout.dto.TopCustomerDTO;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.DatCho;
import com.example.layout.entity.KhachHang;

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
    List<DatCho>  findByChuyenDuLich_Tour_MaTour(@Param("maTour") Integer maTour);
    
    @Query(value = """
    	    SELECT 
    	        tk.HoTen AS customerName,
    	        t.TenTour AS tourName,
    	        dc.NgayDat AS bookingDate,
    	        dc.TrangThai AS status
    	    FROM DatCho dc
    	    JOIN KhachHang kh ON dc.MaKhachHang = kh.MaKhachHang
    	    JOIN TaiKhoan tk ON kh.MaTaiKhoan = tk.MaTaiKhoan
    	    JOIN ChuyenDuLich cdl ON dc.MaChuyen = cdl.MaChuyen
    	    JOIN Tour t ON cdl.MaTour = t.MaTour
    	    ORDER BY dc.NgayDat DESC
    	    LIMIT 5
    	""", nativeQuery = true)
    	List<Object[]> findTop5RecentBookings();

    
    @Query("SELECT COALESCE(SUM(ct.soLuong), 0) " +
            "FROM DatCho dc JOIN ChiTietDatCho ct ON dc.maDatCho = ct.datCho.maDatCho " +
            "WHERE dc.chuyenDuLich.maChuyen = :maChuyen")

     int getTongSoLuongDaDat(@Param("maChuyen") Integer maChuyen);
    
    @Query("""
    	    SELECT d FROM DatCho d
    	    JOIN FETCH d.chuyenDuLich c
    	    JOIN FETCH c.tour t
    	    WHERE d.khachHang.maKhachHang = :maKH
    	    AND c.trangThai = 'Sắp diễn ra'
    	    AND d.trangThai = 'Chưa thanh toán'
    	""")
    	List<DatCho> findDatChoSapDienRaChuaThanhToan(@Param("maKH") Integer maKH);
    Optional<DatCho> findFirstByKhachHangAndChuyenDuLichOrderByNgayDatDesc(KhachHang khachHang, ChuyenDuLich chuyenDuLich);
    @Query("""
    	    SELECT d FROM DatCho d
    	    JOIN d.chuyenDuLich c
    	    WHERE d.khachHang.maKhachHang = :maKH
    	    AND (c.trangThai = 'Sắp diễn ra' OR c.trangThai = 'Đang diễn ra')
    	    AND d.trangThai = 'Đã thanh toán'
    	""")
    	List<DatCho> findVeSapDienRa(@Param("maKH") Integer maKH);



    // thống kê độ phổ biến của tour dựa trên số lượt đặt chỗ
    @Query("SELECT t.tenTour as tenTour, COUNT(d.maDatCho) as soLuotDat " +
            "FROM DatCho d JOIN d.chuyenDuLich c JOIN c.tour t " +
            "GROUP BY t.maTour, t.tenTour ORDER BY soLuotDat DESC")
    List<Map<String, Object>> findTourPopularity();
    
    @Query(value = """
    	    SELECT COALESCE(SUM(ct.ThanhTien), 0)
    	    FROM DatCho dc
    	    JOIN ChiTietDatCho ct ON dc.MaDatCho = ct.MaDatCho
    	    WHERE dc.MaChuyen = :maChuyen
    	    AND dc.TrangThai = 'Đã thanh toán'
    	""", nativeQuery = true)
    	BigDecimal getTotalRevenueByTrip(@Param("maChuyen") Integer maChuyen);
    @Query(value = """
    	    SELECT COALESCE(SUM(ctdc.SoLuong), 0)
    	    FROM DatCho dc
    	    JOIN ChiTietDatCho ctdc ON dc.MaDatCho = ctdc.MaDatCho
    	    WHERE dc.MaChuyen = :maChuyen
    	      AND dc.TrangThai = 'Đã thanh toán'
    	    """, nativeQuery = true)
    	Long getSoldTicketCount(@Param("maChuyen") Integer maChuyen);
    @Query("""
    	    SELECT new com.example.layout.dto.TopCustomerDTO(
    	        kh.maKhachHang,
    	        kh.taiKhoan.hoTen,
    	        SUM(ct.thanhTien)
    	    )
    	    FROM DatCho dc
    	    JOIN dc.khachHang kh
    	    JOIN dc.chiTietDatChos ct
    	    WHERE dc.trangThai = 'Đã thanh toán'
    	    GROUP BY kh.maKhachHang, kh.taiKhoan.hoTen
    	    ORDER BY SUM(ct.thanhTien) DESC
    	""")
    	List<TopCustomerDTO> findTopCustomers();

}

package com.example.layout.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.layout.entity.ChiTietDatCho;

@Repository
public interface ChiTietDatChoRepository extends JpaRepository<ChiTietDatCho, Integer> {
    @Query("SELECT COALESCE(SUM(ct.thanhTien), 0) FROM ChiTietDatCho ct WHERE ct.datCho.maDatCho = :maDatCho")
    BigDecimal findTotalAmountByDatCho_MaDatCho(@Param("maDatCho") Integer maDatCho);

    // tính tổng doanh thu từ chi tiết đặt chỗ theo mã chuyến
    @Query("SELECT COALESCE(SUM(ct.thanhTien), 0) FROM ChiTietDatCho ct WHERE ct.datCho.chuyenDuLich.maChuyen = :maChuyen")
    BigDecimal findTotalRevenueByChuyenId(@Param("maChuyen") Integer maChuyen);
}

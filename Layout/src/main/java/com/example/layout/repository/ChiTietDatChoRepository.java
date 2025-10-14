package com.example.layout.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.layout.entity.ChiTietDatCho;

@Repository
public interface ChiTietDatChoRepository extends JpaRepository<ChiTietDatCho, Integer> {
    @Query("SELECT SUM(ct.thanhTien) FROM ChiTietDatCho ct WHERE ct.datCho.maDatCho = :maDatCho")
    BigDecimal findTotalAmountByDatChoId(@Param("maDatCho") Integer maDatCho);
}

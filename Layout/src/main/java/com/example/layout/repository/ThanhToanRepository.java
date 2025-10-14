package com.example.layout.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.layout.entity.ThanhToan;

@Repository
public interface ThanhToanRepository extends JpaRepository<ThanhToan, Integer> {
    List<ThanhToan> findByDatCho_MaDatCho(Integer maDatCho);

    @Query("SELECT COALESCE(SUM(tt.soTien), 0) FROM ThanhToan tt WHERE tt.datCho.maDatCho = :maDatCho")
    BigDecimal findTotalPaidByDatChoId(@Param("maDatCho") Integer maDatCho);
}

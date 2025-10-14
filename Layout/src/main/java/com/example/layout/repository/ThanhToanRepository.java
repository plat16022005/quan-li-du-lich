package com.example.layout.repository;

import com.example.layout.entity.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface ThanhToanRepository extends JpaRepository<ThanhToan, Integer> {

    @Query("SELECT COALESCE(SUM(t.soTien), 0) FROM ThanhToan t WHERE FUNCTION('MONTH', t.ngayThanhToan) = :month AND FUNCTION('YEAR', t.ngayThanhToan) = :year")
    BigDecimal sumSoTienByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query(value = "SELECT CAST(t.NgayThanhToan AS DATE) as ngay, SUM(t.SoTien) as total " +
           "FROM ThanhToan t " +
           "WHERE YEAR(t.NgayThanhToan) = :year " +
           "AND MONTH(t.NgayThanhToan) = :month " +
           "GROUP BY CAST(t.NgayThanhToan AS DATE)", 
           nativeQuery = true)
    List<Object[]> getExpensesByMonth(@Param("year") int year, @Param("month") int month);
}

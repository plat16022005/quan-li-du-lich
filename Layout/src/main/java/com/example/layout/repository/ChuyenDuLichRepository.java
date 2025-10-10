package com.example.layout.repository;

import com.example.layout.entity.ChuyenDuLich;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}

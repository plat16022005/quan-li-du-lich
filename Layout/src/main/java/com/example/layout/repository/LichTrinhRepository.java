package com.example.layout.repository;

import com.example.layout.entity.LichTrinh;
import com.example.layout.entity.Tour;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichTrinhRepository extends JpaRepository<LichTrinh, Integer> {
    List<LichTrinh> findByTour_MaTour(Integer maTour);
    List<LichTrinh> findByTourOrderByThuTuNgayAsc(Tour tour);
}
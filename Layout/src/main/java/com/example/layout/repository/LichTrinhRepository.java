package com.example.layout.repository;

import com.example.layout.entity.LichTrinh;
import com.example.layout.entity.Tour;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichTrinhRepository extends JpaRepository<LichTrinh, Integer> {
    List<LichTrinh> findByTour_MaTour(Integer maTour);

    List<LichTrinh> findByTourOrderByThuTuNgayAsc(Tour tour);
    List<LichTrinh> findByTour_MaTourOrderByThuTuNgayAsc(Integer maTour);
    
    @Query("SELECT lt FROM LichTrinh lt WHERE lt.tour.maTour = :maTour ORDER BY lt.thuTuNgay ASC")
    List<LichTrinh> findByMaTour(@Param("maTour") Integer maTour);
    
    List<LichTrinh> findByTour(Tour tour);

}


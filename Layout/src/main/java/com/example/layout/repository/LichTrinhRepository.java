package com.example.layout.repository;

import com.example.layout.entity.LichTrinh;
import com.example.layout.entity.Tour;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface LichTrinhRepository extends JpaRepository<LichTrinh, Integer> {
    List<LichTrinh> findByTour_MaTour(Integer maTour);

    List<LichTrinh> findByTourOrderByThuTuNgayAsc(Tour tour);
    List<LichTrinh> findByTour_MaTourOrderByThuTuNgayAsc(Integer maTour);
    
    @Query("SELECT lt FROM LichTrinh lt WHERE lt.tour.maTour = :maTour ORDER BY lt.thuTuNgay ASC")
    List<LichTrinh> findByMaTour(@Param("maTour") Integer maTour);
    
    List<LichTrinh> findByTour(Tour tour);

    // tính toán số lần sử dụng địa điểm trong lịch trình
   // Code đã sửa
    @Query("SELECT dd.tenDiaDiem as tenDiaDiem, COUNT(lt.diaDiem) as soLan " +
        "FROM LichTrinh lt JOIN lt.diaDiem dd " +
        "GROUP BY lt.diaDiem, dd.tenDiaDiem ORDER BY soLan DESC")
    List<Map<String, Object>> findLocationUsageStatistics();
    
}


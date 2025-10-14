package com.example.layout.repository;

import com.example.layout.entity.LichTrinh;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LichTrinhRepository extends JpaRepository<LichTrinh, Integer> {
    List<LichTrinh> findByTour_MaTour(Integer maTour);
}
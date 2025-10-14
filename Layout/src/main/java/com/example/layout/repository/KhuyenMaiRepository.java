package com.example.layout.repository;

import com.example.layout.entity.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer> {

    @Query("SELECT km FROM KhuyenMai km WHERE LOWER(km.tenKM) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(km.maCode) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<KhuyenMai> searchByKeyword(String keyword);
}

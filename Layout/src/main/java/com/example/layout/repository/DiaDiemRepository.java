package com.example.layout.repository;

import com.example.layout.entity.DiaDiem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaDiemRepository extends JpaRepository<DiaDiem, Integer> {
    List<DiaDiem> findByTenDiaDiem(String tenDiaDiem);
    List<DiaDiem> findByTenDiaDiemContainingIgnoreCase(String keyword);
}

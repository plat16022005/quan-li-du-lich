package com.example.layout.service;

import com.example.layout.entity.DiaDiem;

import java.util.List;
import java.util.Optional;

public interface IDiaDiemService {
    List<DiaDiem> findAll();
    Optional<DiaDiem> findById(Integer maDiaDiem);
    DiaDiem save(DiaDiem diaDiem);
    void deleteById(Integer maDiaDiem);
    List<DiaDiem> searchByTen(String keyword);
}

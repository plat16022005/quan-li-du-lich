package com.example.layout.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.layout.entity.DiaDiem;
import com.example.layout.repository.DiaDiemRepository;

@Service
public class DiaDiemService {
    private final DiaDiemRepository diaDiemRepository;

    public DiaDiemService(DiaDiemRepository diaDiemRepository) {
        this.diaDiemRepository = diaDiemRepository;
    }

    public List<DiaDiem> findAll(){
        return diaDiemRepository.findAll();
    }

    public Optional<DiaDiem> findById(Integer maDiaDiem) {
        return diaDiemRepository.findById(maDiaDiem);
    }

    public void save(DiaDiem diaDiem){
        diaDiemRepository.save(diaDiem);
    }

    public void deleteById(Integer maDiaDiem) {
        diaDiemRepository.deleteById(maDiaDiem);
    }

    public List<DiaDiem> searchByTen(String keyword) {
        return diaDiemRepository.findByTenDiaDiemContainingIgnoreCase(keyword);
    }

}
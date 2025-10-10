package com.example.layout.service;

import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.repository.ChuyenDuLichRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChuyenDuLichService {
    private final ChuyenDuLichRepository chuyenDuLichRepository;

    public ChuyenDuLichService(ChuyenDuLichRepository chuyenDuLichRepository) {
        this.chuyenDuLichRepository = chuyenDuLichRepository;
    }

    public Optional<ChuyenDuLich> getNearestChuyen(Integer maTour) {
        return chuyenDuLichRepository.findTopByTour_MaTourOrderByNgayBatDauAsc(maTour);
    }
    
    public int getTotalParticipants(Integer maChuyen) {
        return chuyenDuLichRepository.getTotalParticipants(maChuyen);
    }
    public ChuyenDuLich saveChuyen(ChuyenDuLich chuyen) {
        return chuyenDuLichRepository.save(chuyen);
    }
    public ChuyenDuLich getChuyenById(Integer maChuyen)
    {
    	return chuyenDuLichRepository.findById(maChuyen).orElse(null);
    }
}

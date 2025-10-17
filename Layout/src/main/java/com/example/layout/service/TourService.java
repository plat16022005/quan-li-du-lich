package com.example.layout.service;

import com.example.layout.entity.Tour;
import com.example.layout.repository.TourRepository;

import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
// @RequiredArgsConstructor
public class TourService {
    private final TourRepository tourRepository;

    public TourService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }
    public Tour saveTour(Tour tour) {
        return tourRepository.save(tour);
    }
    @Transactional
    public void deleteTourById(Integer maTour) {
        tourRepository.deleteById(maTour);
    }
    
    public Tour getTourById(Integer maTour) {
        // ✅ Sửa đúng ở đây
        return tourRepository.findById(maTour).orElse(null);
    }
}

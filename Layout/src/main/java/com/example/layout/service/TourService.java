package com.example.layout.service;

import com.example.layout.dto.TourDetailDTO;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.LichTrinh;
import com.example.layout.entity.Tour;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.LichTrinhRepository;
import com.example.layout.repository.TourRepository;

import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
// @RequiredArgsConstructor
public class TourService {
    private final TourRepository tourRepository;
    
    private ChuyenDuLichRepository chuyenDuLichRepository;

    private LichTrinhRepository lichTrinhRepository;

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
    

    public TourDetailDTO getTourDetails(Integer maTour) {
        Tour tour = tourRepository.findById(maTour)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tour với mã: " + maTour));

        // Lấy danh sách các chuyến thuộc tour này
        List<ChuyenDuLich> chuyenDuLichs = tour.getDanhSachChuyen();

        // Lấy lịch trình của tour
        List<LichTrinh> lichTrinhs = lichTrinhRepository.findByTour_MaTour(maTour);

        return new TourDetailDTO(tour, chuyenDuLichs, lichTrinhs);
    }
}
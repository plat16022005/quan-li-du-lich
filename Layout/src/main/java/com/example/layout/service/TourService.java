package com.example.layout.service;

import com.example.layout.dto.TourDetailDTO;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.LichTrinh;
import com.example.layout.entity.Tour;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.LichTrinhRepository;
import com.example.layout.repository.TourRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TourService implements ITourService {
    private final TourRepository tourRepository;
    private final ChuyenDuLichRepository chuyenDuLichRepository;
    private final LichTrinhRepository lichTrinhRepository;

    public TourService(TourRepository tourRepository,
                       ChuyenDuLichRepository chuyenDuLichRepository,
                       LichTrinhRepository lichTrinhRepository) {
        this.tourRepository = tourRepository;
        this.chuyenDuLichRepository = chuyenDuLichRepository;
        this.lichTrinhRepository = lichTrinhRepository;
    }

    @Override
    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }

    @Override
    public Tour saveTour(Tour tour) {
        if (tour.getGiaCoBan() != null && tour.getGiaCoBan().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Giá cơ bản của tour phải lớn hơn 0");
        }
        if (tour.getSoNgay() != null && tour.getSoNgay() <= 0) {
            throw new RuntimeException("Số ngày của tour phải ít nhất là 1");
        }
        return tourRepository.save(tour);
    }

    @Override
    @Transactional
    public void deleteTourById(Integer maTour) {
        tourRepository.deleteById(maTour);
    }

    @Override
    public Tour getTourById(Integer maTour) {
        return tourRepository.findById(maTour).orElse(null);
    }

    @Override
    public TourDetailDTO getTourDetails(Integer maTour) {
        Tour tour = tourRepository.findById(maTour)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tour với mã: " + maTour));

        List<ChuyenDuLich> chuyenDuLichs = tour.getDanhSachChuyen();
        List<LichTrinh> lichTrinhs = lichTrinhRepository.findByTour_MaTour(maTour);

        return new TourDetailDTO(tour, chuyenDuLichs, lichTrinhs);
    }
}

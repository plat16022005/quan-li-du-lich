package com.example.layout.service;

import com.example.layout.dto.TourDetailDTO;
import com.example.layout.entity.Tour;

import java.util.List;

public interface ITourService {
    List<Tour> getAllTours();
    Tour saveTour(Tour tour);
    void deleteTourById(Integer maTour);
    Tour getTourById(Integer maTour);
    TourDetailDTO getTourDetails(Integer maTour);
}

package com.example.layout.service;

import com.example.layout.entity.ChuyenDuLich;

import java.util.List;

public interface INotificationService {
    List<ChuyenDuLich> getUpcomingTrips(Integer staffId, int daysAhead);
    int countUpcomingTrips(Integer staffId, int daysAhead);
    String generateNotificationMessage(ChuyenDuLich trip);
}

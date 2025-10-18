// Layout/src/main/java/com/example/layout/service/NotificationService.java
package com.example.layout.service;

import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.repository.ChuyenDuLichRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private ChuyenDuLichRepository chuyenDuLichRepository;

    /**
     * Lấy các chuyến sắp khởi hành trong 7 ngày tới
     */
    public List<ChuyenDuLich> getUpcomingTrips(Integer staffId, int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(daysAhead);
        
        return chuyenDuLichRepository.findUpcomingTripsByStaff(today, endDate, staffId);
    }

    /**
     * Đếm số chuyến sắp khởi hành
     */
    public int countUpcomingTrips(Integer staffId, int daysAhead) {
        return getUpcomingTrips(staffId, daysAhead).size();
    }

    /**
     * Tạo thông báo cho từng chuyến
     */
    public String generateNotificationMessage(ChuyenDuLich trip) {
        long daysUntilStart = ChronoUnit.DAYS.between(LocalDate.now(), trip.getNgayBatDau());
        
        String tourName = trip.getTour() != null ? trip.getTour().getTenTour() : "Chuyến #" + trip.getMaChuyen();
        
        if (daysUntilStart == 0) {
            return "🚀 Chuyến '" + tourName + "' khởi hành HÔM NAY!";
        } else if (daysUntilStart == 1) {
            return "⏰ Chuyến '" + tourName + "' khởi hành VÀO NGÀY MAI!";
        } else {
            return "📅 Chuyến '" + tourName + "' khởi hành sau " + daysUntilStart + " ngày";
        }
    }
}
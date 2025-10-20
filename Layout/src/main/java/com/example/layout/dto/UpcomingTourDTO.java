package com.example.layout.dto;

import java.time.LocalDate;

public class UpcomingTourDTO {
    private String tenTour;
    private LocalDate ngayBatDau;

    public UpcomingTourDTO(String tenTour, LocalDate ngayBatDau) {
        this.tenTour = tenTour;
        this.ngayBatDau = ngayBatDau;
    }

    public String getTenTour() {
        return tenTour;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }
}

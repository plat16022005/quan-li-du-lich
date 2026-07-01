package com.example.layout.dto;

import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.LichTrinh;
import com.example.layout.entity.Tour;

import java.util.List;

public class TourDetailDTO {
    private Tour tour;
    private List<ChuyenDuLich> danhSachChuyen;
    private List<LichTrinh> lichTrinh;

    public TourDetailDTO(Tour tour, List<ChuyenDuLich> danhSachChuyen, List<LichTrinh> lichTrinh) {
        this.tour = tour;
        this.danhSachChuyen = danhSachChuyen;
        this.lichTrinh = lichTrinh;
    }

    // Getters and Setters
    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }
    public List<ChuyenDuLich> getDanhSachChuyen() { return danhSachChuyen; }
    public void setDanhSachChuyen(List<ChuyenDuLich> danhSachChuyen) { this.danhSachChuyen = danhSachChuyen; }
    public List<LichTrinh> getLichTrinh() { return lichTrinh; }
    public void setLichTrinh(List<LichTrinh> lichTrinh) { this.lichTrinh = lichTrinh; }
}
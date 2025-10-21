package com.example.layout.dto;

import java.time.LocalDate;

import com.example.layout.entity.DatCho;

public class BookingHistoryDTO {
    // DTO đơn giản cho danh sách lịch sử booking của khách hàng
    private Integer maDatCho;
    private String tenTour;
    private LocalDate ngayDat;
    private String trangThai;

    // Constructor, Getters, Setters
    
    // Bạn có thể tạo constructor nhận vào Entity DatCho
    public BookingHistoryDTO(DatCho datCho) {
        this.maDatCho = datCho.getMaDatCho();
        this.tenTour = datCho.getChuyenDuLich().getTour().getTenTour(); // Lấy tên tour
        this.ngayDat = datCho.getNgayDat();
        this.trangThai = datCho.getTrangThai();
    }

    public Integer getMaDatCho() {
        return maDatCho;
    }

    public void setMaDatCho(Integer maDatCho) {
        this.maDatCho = maDatCho;
    }

    public String getTenTour() {
        return tenTour;
    }

    public void setTenTour(String tenTour) {
        this.tenTour = tenTour;
    }

    public LocalDate getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(LocalDate ngayDat) {
        this.ngayDat = ngayDat;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}


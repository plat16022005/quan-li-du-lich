package com.example.layout.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingResponseDTO {
    private Integer maDatCho;
    private String tenTour;
    private LocalDate ngayKhoiHanh;
    private BigDecimal tongTien;

    // getters và setters
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
    public LocalDate getNgayKhoiHanh() {
        return ngayKhoiHanh;
    }
    public void setNgayKhoiHanh(LocalDate ngayKhoiHanh) {
        this.ngayKhoiHanh = ngayKhoiHanh;
    }
    public BigDecimal getTongTien() {
        return tongTien;
    }
    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }
}

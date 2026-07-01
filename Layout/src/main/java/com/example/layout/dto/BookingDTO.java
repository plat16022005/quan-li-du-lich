package com.example.layout.dto;

import java.time.LocalDate;
import java.util.Date;

public class BookingDTO {
    private Integer maDangKy;
    private String hoTen;
    private String soDienThoai;
    private LocalDate ngayDangKy;
    private String trangThai;

    public BookingDTO(Integer maDangKy, String hoTen, String soDienThoai, LocalDate localDate, String trangThai) {
        this.maDangKy = maDangKy;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.ngayDangKy = localDate;
        this.trangThai = trangThai;
    }

    // Getters
    public Integer getMaDangKy() {
        return maDangKy;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public LocalDate getNgayDangKy() {
        return ngayDangKy;
    }

    public String getTrangThai() {
        return trangThai;
    }

    // Setters
    public void setMaDangKy(Integer maDangKy) {
        this.maDangKy = maDangKy;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public void setNgayDangKy(LocalDate ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}

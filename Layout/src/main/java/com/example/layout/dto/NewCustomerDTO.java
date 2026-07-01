package com.example.layout.dto;

import java.time.LocalDate;

public class NewCustomerDTO {
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String bietDen;

    // Default constructor
    public NewCustomerDTO() {}

    // Getters and setters
    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getBietDen() {
        return bietDen;
    }

    public void setBietDen(String bietDen) {
        this.bietDen = bietDen;
    }
}
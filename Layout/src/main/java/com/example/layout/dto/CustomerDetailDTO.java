package com.example.layout.dto;

import java.time.LocalDate;

import com.example.layout.entity.KhachHang;

public class CustomerDetailDTO {
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private LocalDate ngayThamGia;
    
    // Constructors, Getters, Setters
    public CustomerDetailDTO(KhachHang customer) {
        this.hoTen = customer.getTaiKhoan().getHoTen();
        this.email = customer.getTaiKhoan().getEmail();
        this.soDienThoai = customer.getTaiKhoan().getSoDienThoai();
        this.diaChi = customer.getDiaChi();
        this.ngaySinh = customer.getNgaySinh();
        this.gioiTinh = customer.getGioiTinh();
        this.ngayThamGia = customer.getNgayThamGia();
    }

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

    public LocalDate getNgayThamGia() {
        return ngayThamGia;
    }

    public void setNgayThamGia(LocalDate ngayThamGia) {
        this.ngayThamGia = ngayThamGia;
    }
}
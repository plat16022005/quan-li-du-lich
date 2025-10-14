package com.example.layout.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DatChoDTO {
    @NotNull(message = "Mã khách hàng không được để trống")
    private Integer maKhachHang;

    @NotNull(message = "Mã chuyến đi không được để trống")
    private Integer maChuyen;

    @Min(value = 1, message = "Số lượng phải ít nhất là 1")
    private int soLuong;

    // Mã khuyến mãi (không bắt buộc)
    private String maKhuyenMai;

    // Getters and Setters
    public Integer getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(Integer maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public Integer getMaChuyen() {
        return maChuyen;
    }

    public void setMaChuyen(Integer maChuyen) {
        this.maChuyen = maChuyen;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }
}

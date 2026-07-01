package com.example.layout.dto;

import java.math.BigDecimal;

public class TopCustomerDTO {
    private Integer maKhachHang;
    private String hoTen;
    private BigDecimal tongChiTieu;

    public TopCustomerDTO(Integer maKhachHang, String hoTen, BigDecimal tongChiTieu) {
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.tongChiTieu = tongChiTieu;
    }

    public Integer getMaKhachHang() { return maKhachHang; }
    public String getHoTen() { return hoTen; }
    public BigDecimal getTongChiTieu() { return tongChiTieu; }
}

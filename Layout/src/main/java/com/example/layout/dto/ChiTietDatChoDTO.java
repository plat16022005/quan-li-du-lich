package com.example.layout.dto;

import java.math.BigDecimal;

public class ChiTietDatChoDTO {
    private Integer maChiTiet;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private String loaiVe;

    public ChiTietDatChoDTO() {}

    public ChiTietDatChoDTO(Integer maChiTiet, Integer soLuong, BigDecimal donGia, BigDecimal thanhTien, String loaiVe) {
        this.maChiTiet = maChiTiet;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
        this.loaiVe = loaiVe;
    }

    public Integer getMaChiTiet() {
        return maChiTiet;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public String getLoaiVe() {
        return loaiVe;
    }

    public void setMaChiTiet(Integer maChiTiet) {
        this.maChiTiet = maChiTiet;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    public void setLoaiVe(String loaiVe) {
        this.loaiVe = loaiVe;
    }
}

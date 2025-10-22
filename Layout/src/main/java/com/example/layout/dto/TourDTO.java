package com.example.layout.dto;

import java.math.BigDecimal;

public class TourDTO {
    private Integer maTour;
    private String tenTour;
    private BigDecimal giaCoBan;
    private Integer soNgay;
    private String moTa;
    private String hinhAnh;

    public TourDTO(Integer maTour, String tenTour, BigDecimal giaCoBan, Integer soNgay, String moTa, String hinhAnh) {
        this.maTour = maTour;
        this.tenTour = tenTour;
        this.giaCoBan = giaCoBan;
        this.soNgay = soNgay;
        this.moTa = moTa;
        this.hinhAnh = hinhAnh;
    }

    public Integer getMaTour() {
        return maTour;
    }

    public void setMaTour(Integer maTour) {
        this.maTour = maTour;
    }

    public String getTenTour() {
        return tenTour;
    }

    public void setTenTour(String tenTour) {
        this.tenTour = tenTour;
    }

    public BigDecimal getGiaCoBan() {
        return giaCoBan;
    }

    public void setGiaCoBan(BigDecimal giaCoBan) {
        this.giaCoBan = giaCoBan;
    }

    public Integer getSoNgay() {
        return soNgay;
    }

    public void setSoNgay(Integer soNgay) {
        this.soNgay = soNgay;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    
    
}

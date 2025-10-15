package com.example.layout.dto;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ThanhToanDTO {
    @NotNull(message = "Mã đặt chỗ không được để trống")
    private Integer maDatCho;

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "1.0", message = "Số tiền thanh toán phải lớn hơn 0")
    private BigDecimal soTien;

    private Date ngayThanhToan;

    @NotBlank(message = "Vui lòng nhập hình thức thanh toán")
    private String hinhThuc; // Ví dụ: "Chuyển khoản", "Tiền mặt"

    // Getters and Setters
    public Integer getMaDatCho() {
        return maDatCho;
    }

    public void setMaDatCho(Integer maDatCho) {
        this.maDatCho = maDatCho;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public Date getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(Date ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public String getHinhThuc() {
        return hinhThuc;
    }

    public void setHinhThuc(String hinhThuc) {
        this.hinhThuc = hinhThuc;
    }
}

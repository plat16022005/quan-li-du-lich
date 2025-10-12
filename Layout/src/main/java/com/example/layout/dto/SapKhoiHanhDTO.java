package com.example.layout.dto;

import java.time.LocalDate;

public class SapKhoiHanhDTO {
    private String tenTour;
    private LocalDate ngayDi;
    private int soKhach;
    private int soLuongToiDa;

    public SapKhoiHanhDTO(String tenTour, LocalDate ngayDi, int soKhach, int soLuongToiDa) {
        this.tenTour = tenTour;
        this.ngayDi = ngayDi;
        this.soKhach = soKhach;
        this.soLuongToiDa = soLuongToiDa;
    }

    public String getTenTour() { return tenTour; }
    public LocalDate getNgayDi() { return ngayDi; }
    public int getSoKhach() { return soKhach; }
    public int getSoLuongToiDa() { return soLuongToiDa; }
}

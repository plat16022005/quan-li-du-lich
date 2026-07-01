package com.example.layout.dto;

public class CurrentUserDTO {
    private final String hoTen;
    private final String chucVu;
    private final int maVaiTro;
    private final int maNhanVien;

    public CurrentUserDTO(String hoTen, String chucVu, int maVaiTro, int maNhanVien) {
        this.hoTen = hoTen;
        this.chucVu = chucVu;
        this.maVaiTro = maVaiTro;
        this.maNhanVien = maNhanVien;
    }

    public String getHoTen() { return hoTen; }
    public String getChucVu() { return chucVu; }
    public int getMaVaiTro() { return maVaiTro; }
    public int getMaNhanVien() { return maNhanVien; }
}
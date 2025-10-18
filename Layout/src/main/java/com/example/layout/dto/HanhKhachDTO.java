package com.example.layout.dto;


public class HanhKhachDTO {
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String gioiTinh;
    private Long soLuong; 

    public HanhKhachDTO(String hoTen, String soDienThoai, String email, String gioiTinh, Long soLuong) {
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.gioiTinh = gioiTinh;
        this.soLuong = soLuong != null ? soLuong : 1L;
    }

    // ✅ Constructor CŨ - 4 tham số (để tương thích)
    public HanhKhachDTO(String hoTen, String soDienThoai, String email, String gioiTinh) {
        this(hoTen, soDienThoai, email, gioiTinh, 1L);
    }

    // Getters & Setters
    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public Long getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Long soLuong) {
        this.soLuong = soLuong;
    }
}
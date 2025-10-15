package com.example.layout.dto;

import java.util.Date;
import java.util.List;

public class BookingApiDTO {
    private Integer maDatCho;
    private Date ngayDat;
    private String trangThai;
    private KhachHangSummary khachHang;
    private ChuyenSummary chuyenDuLich;
    private List<ChiTietDatChoDTO> chiTietDatCho;

    public BookingApiDTO() {}

    public BookingApiDTO(Integer maDatCho, Date ngayDat, String trangThai, KhachHangSummary khachHang, ChuyenSummary chuyenDuLich, List<ChiTietDatChoDTO> chiTietDatCho) {
        this.maDatCho = maDatCho;
        this.ngayDat = ngayDat;
        this.trangThai = trangThai;
        this.khachHang = khachHang;
        this.chuyenDuLich = chuyenDuLich;
        this.chiTietDatCho = chiTietDatCho;
    }

    public Integer getMaDatCho() { return maDatCho; }
    public Date getNgayDat() { return ngayDat; }
    public String getTrangThai() { return trangThai; }
    public KhachHangSummary getKhachHang() { return khachHang; }
    public ChuyenSummary getChuyenDuLich() { return chuyenDuLich; }
    public List<ChiTietDatChoDTO> getChiTietDatCho() { return chiTietDatCho; }

    public void setMaDatCho(Integer maDatCho) { this.maDatCho = maDatCho; }
    public void setNgayDat(Date ngayDat) { this.ngayDat = ngayDat; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void setKhachHang(KhachHangSummary khachHang) { this.khachHang = khachHang; }
    public void setChuyenDuLich(ChuyenSummary chuyenDuLich) { this.chuyenDuLich = chuyenDuLich; }
    public void setChiTietDatCho(List<ChiTietDatChoDTO> chiTietDatCho) { this.chiTietDatCho = chiTietDatCho; }

    public static class KhachHangSummary {
        private Integer maKhachHang;
        private String hoTen;
        public KhachHangSummary() {}
        public KhachHangSummary(Integer maKhachHang, String hoTen) { this.maKhachHang = maKhachHang; this.hoTen = hoTen; }
        public Integer getMaKhachHang() { return maKhachHang; }
        public String getHoTen() { return hoTen; }
        public void setMaKhachHang(Integer maKhachHang) { this.maKhachHang = maKhachHang; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    }

    public static class ChuyenSummary {
        private Integer maChuyen;
        private String tenTour;
        public ChuyenSummary() {}
        public ChuyenSummary(Integer maChuyen, String tenTour) { this.maChuyen = maChuyen; this.tenTour = tenTour; }
        public Integer getMaChuyen() { return maChuyen; }
        public String getTenTour() { return tenTour; }
        public void setMaChuyen(Integer maChuyen) { this.maChuyen = maChuyen; }
        public void setTenTour(String tenTour) { this.tenTour = tenTour; }
    }
}

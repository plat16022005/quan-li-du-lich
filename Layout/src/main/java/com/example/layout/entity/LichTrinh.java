package com.example.layout.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "LichTrinh")
public class LichTrinh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaLichTrinh")
    private Integer maLichTrinh;

    @ManyToOne
    @JoinColumn(name = "MaTour")
    @JsonIgnore
    private Tour tour;

    @Column(name = "ThuTuNgay")
    private Integer thuTuNgay;

    @Column(name = "HoatDong", length = 500)
    private String hoatDong;

    @ManyToOne
    @JoinColumn(name = "MaDiaDiem")
    private DiaDiem diaDiem;

    // 🏨 Thêm quan hệ với KhachSan
    @ManyToOne
    @JoinColumn(name = "MaKhachSan")
    private KhachSan khachSan;

    // 🚍 Thêm quan hệ với PhuongTien
    @ManyToOne
    @JoinColumn(name = "MaPhuongTien")
    private PhuongTien phuongTien;

    public Integer getMaLichTrinh() {
        return maLichTrinh;
    }

    public void setMaLichTrinh(Integer maLichTrinh) {
        this.maLichTrinh = maLichTrinh;
    }

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }

    public Integer getThuTuNgay() {
        return thuTuNgay;
    }

    public void setThuTuNgay(Integer thuTuNgay) {
        this.thuTuNgay = thuTuNgay;
    }

    public String getHoatDong() {
        return hoatDong;
    }

    public void setHoatDong(String hoatDong) {
        this.hoatDong = hoatDong;
    }

    public DiaDiem getDiaDiem() {
        return diaDiem;
    }

    public void setDiaDiem(DiaDiem diaDiem) {
        this.diaDiem = diaDiem;
    }

    public KhachSan getKhachSan() {
        return khachSan;
    }

    public void setKhachSan(KhachSan khachSan) {
        this.khachSan = khachSan;
    }

    public PhuongTien getPhuongTien() {
        return phuongTien;
    }

    public void setPhuongTien(PhuongTien phuongTien) {
        this.phuongTien = phuongTien;
    }
}

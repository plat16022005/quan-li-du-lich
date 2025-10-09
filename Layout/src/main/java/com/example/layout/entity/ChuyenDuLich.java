package com.example.layout.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "ChuyenDuLich")
public class ChuyenDuLich {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaChuyen")
    private Integer maChuyen;

    @ManyToOne
    @JoinColumn(name = "MaTour")
    private Tour tour;

    @Column(name = "NgayBatDau")
    @Temporal(TemporalType.DATE)
    private Date ngayBatDau;

    @Column(name = "NgayKetThuc")
    @Temporal(TemporalType.DATE)
    private Date ngayKetThuc;

    @Column(name = "TrangThai", length = 20)
    private String trangThai;

    @ManyToOne
    @JoinColumn(name = "MaHuongDanVien")
    private Nhanvien huongDanVien;

    @ManyToOne
    @JoinColumn(name = "MaTaiXe")
    private Nhanvien taiXe;

    @ManyToMany
    @JoinTable(
        name = "Chuyen_KhachSan",
        joinColumns = @JoinColumn(name = "MaChuyen"),
        inverseJoinColumns = @JoinColumn(name = "MaKhachSan")
    )
    private Set<KhachSan> khachSans;

    @ManyToMany
    @JoinTable(
        name = "Chuyen_PhuongTien",
        joinColumns = @JoinColumn(name = "MaChuyen"),
        inverseJoinColumns = @JoinColumn(name = "MaPhuongTien")
    )
    private Set<PhuongTien> phuongTiens;

	public Integer getMaChuyen() {
		return maChuyen;
	}

	public void setMaChuyen(Integer maChuyen) {
		this.maChuyen = maChuyen;
	}

	public Tour getTour() {
		return tour;
	}

	public void setTour(Tour tour) {
		this.tour = tour;
	}

	public Date getNgayBatDau() {
		return ngayBatDau;
	}

	public void setNgayBatDau(Date ngayBatDau) {
		this.ngayBatDau = ngayBatDau;
	}

	public Date getNgayKetThuc() {
		return ngayKetThuc;
	}

	public void setNgayKetThuc(Date ngayKetThuc) {
		this.ngayKetThuc = ngayKetThuc;
	}

	public String getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}

	public Nhanvien getHuongDanVien() {
		return huongDanVien;
	}

	public void setHuongDanVien(Nhanvien huongDanVien) {
		this.huongDanVien = huongDanVien;
	}

	public Nhanvien getTaiXe() {
		return taiXe;
	}

	public void setTaiXe(Nhanvien taiXe) {
		this.taiXe = taiXe;
	}

	public Set<KhachSan> getKhachSans() {
		return khachSans;
	}

	public void setKhachSans(Set<KhachSan> khachSans) {
		this.khachSans = khachSans;
	}

	public Set<PhuongTien> getPhuongTiens() {
		return phuongTiens;
	}

	public void setPhuongTiens(Set<PhuongTien> phuongTiens) {
		this.phuongTiens = phuongTiens;
	}
    
    // Getters and setters
    
}
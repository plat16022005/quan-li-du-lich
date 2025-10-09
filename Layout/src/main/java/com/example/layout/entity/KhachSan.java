package com.example.layout.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "KhachSan")
public class KhachSan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKhachSan")
    private Integer maKhachSan;

    @Column(name = "TenKhachSan", length = 200)
    private String tenKhachSan;

    @Column(name = "DiaChi", length = 300)
    private String diaChi;

    @Column(name = "SoDienThoai", length = 20)
    private String soDienThoai;

    @ManyToMany(mappedBy = "khachSans")
    private Set<ChuyenDuLich> chuyenDuLichs;

	public Integer getMaKhachSan() {
		return maKhachSan;
	}

	public void setMaKhachSan(Integer maKhachSan) {
		this.maKhachSan = maKhachSan;
	}

	public String getTenKhachSan() {
		return tenKhachSan;
	}

	public void setTenKhachSan(String tenKhachSan) {
		this.tenKhachSan = tenKhachSan;
	}

	public String getDiaChi() {
		return diaChi;
	}

	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}

	public String getSoDienThoai() {
		return soDienThoai;
	}

	public void setSoDienThoai(String soDienThoai) {
		this.soDienThoai = soDienThoai;
	}

	public Set<ChuyenDuLich> getChuyenDuLichs() {
		return chuyenDuLichs;
	}

	public void setChuyenDuLichs(Set<ChuyenDuLich> chuyenDuLichs) {
		this.chuyenDuLichs = chuyenDuLichs;
	}

    // Getters and setters
    
    
    
}
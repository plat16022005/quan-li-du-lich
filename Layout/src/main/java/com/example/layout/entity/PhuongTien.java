package com.example.layout.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "PhuongTien")
public class PhuongTien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaPhuongTien")
    private Integer maPhuongTien;

    @Column(name = "LoaiPhuongTien", length = 50)
    private String loaiPhuongTien;

    @Column(name = "BienSo", length = 20)
    private String bienSo;

    @Column(name = "SoChoNgoi")
    private Integer soChoNgoi;

//    @ManyToMany(mappedBy = "phuongTiens")
//    private Set<ChuyenDuLich> chuyenDuLichs;

	public Integer getMaPhuongTien() {
		return maPhuongTien;
	}

	public void setMaPhuongTien(Integer maPhuongTien) {
		this.maPhuongTien = maPhuongTien;
	}

	public String getLoaiPhuongTien() {
		return loaiPhuongTien;
	}

	public void setLoaiPhuongTien(String loaiPhuongTien) {
		this.loaiPhuongTien = loaiPhuongTien;
	}

	public String getBienSo() {
		return bienSo;
	}

	public void setBienSo(String bienSo) {
		this.bienSo = bienSo;
	}

	public Integer getSoChoNgoi() {
		return soChoNgoi;
	}

	public void setSoChoNgoi(Integer soChoNgoi) {
		this.soChoNgoi = soChoNgoi;
	}

//	public Set<ChuyenDuLich> getChuyenDuLichs() {
//		return chuyenDuLichs;
//	}
//
//	public void setChuyenDuLichs(Set<ChuyenDuLich> chuyenDuLichs) {
//		this.chuyenDuLichs = chuyenDuLichs;
//	}

    // Getters and setters
    
    
    
}
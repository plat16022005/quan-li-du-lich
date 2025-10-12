package com.example.layout.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
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

	@Column(name = "GiaTheoNgay", precision = 18, scale = 2)
    private BigDecimal giaTheoNgay;

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
	
    public BigDecimal getGiaTheoNgay() {
		return giaTheoNgay;
	}

	public void setGiaTheoNgay(BigDecimal giaTheoNgay) {
		this.giaTheoNgay = giaTheoNgay;
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
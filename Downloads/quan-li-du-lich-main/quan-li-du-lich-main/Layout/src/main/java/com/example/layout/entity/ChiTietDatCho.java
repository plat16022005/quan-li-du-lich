package com.example.layout.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietDatCho")
public class ChiTietDatCho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaChiTiet")
    private Integer maChiTiet;

    @ManyToOne
    @JoinColumn(name = "MaDatCho")
    private DatCho datCho;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "DonGia", precision = 18, scale = 2)
    private BigDecimal donGia;

    @Column(name = "ThanhTien", precision = 18, scale = 2)
    private BigDecimal thanhTien;

    @Column(name = "LoaiVe", length = 50)
    private String loaiVe;

	public Integer getMaChiTiet() {
		return maChiTiet;
	}

	public void setMaChiTiet(Integer maChiTiet) {
		this.maChiTiet = maChiTiet;
	}

	public DatCho getDatCho() {
		return datCho;
	}

	public void setDatCho(DatCho datCho) {
		this.datCho = datCho;
	}

	public Integer getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(Integer soLuong) {
		this.soLuong = soLuong;
	}

	public BigDecimal getDonGia() {
		return donGia;
	}

	public void setDonGia(BigDecimal donGia) {
		this.donGia = donGia;
	}

	public BigDecimal getThanhTien() {
		return thanhTien;
	}

	public void setThanhTien(BigDecimal thanhTien) {
		this.thanhTien = thanhTien;
	}

	public String getLoaiVe() {
		return loaiVe;
	}

	public void setLoaiVe(String loaiVe) {
		this.loaiVe = loaiVe;
	}
    
    // Getters and setters
    
}
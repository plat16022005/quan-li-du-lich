package com.example.layout.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "GiaoDich")
public class GiaoDich {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaGiaoDich")
    private Integer maGiaoDich;

    @ManyToOne
    @JoinColumn(name = "MaKhachHang")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "MaDatCho")
    private DatCho datCho;

    @Column(name = "SoTien", precision = 18, scale = 2)
    private BigDecimal soTien;

    @Column(name = "NgayGiaoDich")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayGiaoDich;

    @Column(name = "LoaiGD", length = 20)
    private String loaiGD;

	public Integer getMaGiaoDich() {
		return maGiaoDich;
	}

	public void setMaGiaoDich(Integer maGiaoDich) {
		this.maGiaoDich = maGiaoDich;
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public DatCho getDatCho() {
		return datCho;
	}

	public void setDatCho(DatCho datCho) {
		this.datCho = datCho;
	}

	public BigDecimal getSoTien() {
		return soTien;
	}

	public void setSoTien(BigDecimal soTien) {
		this.soTien = soTien;
	}

	public Date getNgayGiaoDich() {
		return ngayGiaoDich;
	}

	public void setNgayGiaoDich(Date ngayGiaoDich) {
		this.ngayGiaoDich = ngayGiaoDich;
	}

	public String getLoaiGD() {
		return loaiGD;
	}

	public void setLoaiGD(String loaiGD) {
		this.loaiGD = loaiGD;
	}

    // Getters and setters
    
}

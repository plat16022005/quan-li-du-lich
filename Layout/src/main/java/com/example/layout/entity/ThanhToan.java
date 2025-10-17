package com.example.layout.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "ThanhToan")
public class ThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaThanhToan")
    private Integer maThanhToan;

    @ManyToOne
    @JoinColumn(name = "MaDatCho")
    private DatCho datCho;

    @Column(name = "NgayThanhToan")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayThanhToan;

    @Column(name = "SoTien", precision = 18, scale = 2)
    private BigDecimal soTien;

    @Column(name = "HinhThuc", length = 50)
    private String hinhThuc;

	public Integer getMaThanhToan() {
		return maThanhToan;
	}

	public void setMaThanhToan(Integer maThanhToan) {
		this.maThanhToan = maThanhToan;
	}

	public DatCho getDatCho() {
		return datCho;
	}

	public void setDatCho(DatCho datCho) {
		this.datCho = datCho;
	}

	public Date getNgayThanhToan() {
		return ngayThanhToan;
	}

	public void setNgayThanhToan(Date ngayThanhToan) {
		this.ngayThanhToan = ngayThanhToan;
	}

	public BigDecimal getSoTien() {
		return soTien;
	}

	public void setSoTien(BigDecimal soTien) {
		this.soTien = soTien;
	}

	public String getHinhThuc() {
		return hinhThuc;
	}

	public void setHinhThuc(String hinhThuc) {
		this.hinhThuc = hinhThuc;
	}

    // Getters and setters
    
    
    
}

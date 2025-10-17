package com.example.layout.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
//import java.util.Date;
//import java.util.Set;

@Entity
@Table(name = "KhuyenMai")
public class KhuyenMai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKM")
    private Integer maKM;

    @Column(name = "TenKM", length = 100)
    private String tenKM;

    @Column(name = "TiLeGiam", precision = 5, scale = 2)
    private BigDecimal tiLeGiam;

    @Column(name = "NgayBatDau")
    private LocalDate ngayBatDau;

    @Column(name = "NgayKetThuc")
    private LocalDate ngayKetThuc;

    @Column(name = "MoTa", length = 500)
    private String moTa;
    
    @Column(name = "MaCode", length = 50)
    private String maCode;

    public String getMaCode() {
        return maCode;
    }
    public void setMaCode(String maCode) {
        this.maCode = maCode;
    }
	public Integer getMaKM() {
		return maKM;
	}

	public void setMaKM(Integer maKM) {
		this.maKM = maKM;
	}

	public String getTenKM() {
		return tenKM;
	}

	public void setTenKM(String tenKM) {
		this.tenKM = tenKM;
	}

	public BigDecimal getTiLeGiam() {
		return tiLeGiam;
	}

	public void setTiLeGiam(BigDecimal tiLeGiam) {
		this.tiLeGiam = tiLeGiam;
	}

	public LocalDate getNgayBatDau() {
		return ngayBatDau;
	}

	public void setNgayBatDau(LocalDate ngayBatDau) {
		this.ngayBatDau = ngayBatDau;
	}

	public LocalDate getNgayKetThuc() {
		return ngayKetThuc;
	}

	public void setNgayKetThuc(LocalDate ngayKetThuc) {
		this.ngayKetThuc = ngayKetThuc;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}
    // Getters and setters
    
    
    
}

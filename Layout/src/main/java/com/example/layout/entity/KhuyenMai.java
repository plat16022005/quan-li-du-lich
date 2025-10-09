package com.example.layout.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

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
    @Temporal(TemporalType.DATE)
    private Date ngayBatDau;

    @Column(name = "NgayKetThuc")
    @Temporal(TemporalType.DATE)
    private Date ngayKetThuc;

    @Column(name = "MoTa", length = 500)
    private String moTa;

    @ManyToMany(mappedBy = "khuyenMais")
    private Set<DatCho> datChos;

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

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	public Set<DatCho> getDatChos() {
		return datChos;
	}

	public void setDatChos(Set<DatCho> datChos) {
		this.datChos = datChos;
	}

    // Getters and setters
    
    
    
}
package com.example.layout.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "Tour")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTour")
    private Integer maTour;

    @Column(name = "TenTour", nullable = false, length = 200)
    private String tenTour;

    @Column(name = "MoTa")
    private String moTa;

    @Column(name = "SoNgay")
    private Integer soNgay;

    @Column(name = "GiaCoBan", precision = 18, scale = 2)
    private BigDecimal giaCoBan;

    @ManyToMany
    @JoinTable(
        name = "Tour_DiaDiem",
        joinColumns = @JoinColumn(name = "MaTour"),
        inverseJoinColumns = @JoinColumn(name = "MaDiaDiem")
    )
    private Set<DiaDiem> diaDiems;

	public Integer getMaTour() {
		return maTour;
	}

	public void setMaTour(Integer maTour) {
		this.maTour = maTour;
	}

	public String getTenTour() {
		return tenTour;
	}

	public void setTenTour(String tenTour) {
		this.tenTour = tenTour;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	public Integer getSoNgay() {
		return soNgay;
	}

	public void setSoNgay(Integer soNgay) {
		this.soNgay = soNgay;
	}

	public BigDecimal getGiaCoBan() {
		return giaCoBan;
	}

	public void setGiaCoBan(BigDecimal giaCoBan) {
		this.giaCoBan = giaCoBan;
	}

	public Set<DiaDiem> getDiaDiems() {
		return diaDiems;
	}

	public void setDiaDiems(Set<DiaDiem> diaDiems) {
		this.diaDiems = diaDiems;
	}

    // Getters and setters
    
    
    
}
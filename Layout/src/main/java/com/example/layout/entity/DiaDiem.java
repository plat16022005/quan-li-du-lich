package com.example.layout.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "DiaDiem")
public class DiaDiem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDiaDiem")
    private Integer maDiaDiem;

    @Column(name = "TenDiaDiem", length = 200)
    private String tenDiaDiem;

    @Column(name = "DiaChi", length = 300)
    private String diaChi;

    @ManyToMany(mappedBy = "diaDiems")
    private Set<Tour> tours;

	public Integer getMaDiaDiem() {
		return maDiaDiem;
	}

	public void setMaDiaDiem(Integer maDiaDiem) {
		this.maDiaDiem = maDiaDiem;
	}

	public String getTenDiaDiem() {
		return tenDiaDiem;
	}

	public void setTenDiaDiem(String tenDiaDiem) {
		this.tenDiaDiem = tenDiaDiem;
	}

	public String getDiaChi() {
		return diaChi;
	}

	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}

	public Set<Tour> getTours() {
		return tours;
	}

	public void setTours(Set<Tour> tours) {
		this.tours = tours;
	}

    // Getters and setters
    
}
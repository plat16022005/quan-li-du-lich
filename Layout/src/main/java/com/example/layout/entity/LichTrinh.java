package com.example.layout.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "LichTrinh")
public class LichTrinh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaLichTrinh")
    private Integer maLichTrinh;

    @ManyToOne
    @JoinColumn(name = "MaTour")
    private Tour tour;

    @Column(name = "ThuTuNgay")
    private Integer thuTuNgay;

    @Column(name = "HoatDong", length = 500)
    private String hoatDong;

    @ManyToOne
    @JoinColumn(name = "MaDiaDiem")
    private DiaDiem diaDiem;

	public Integer getMaLichTrinh() {
		return maLichTrinh;
	}

	public void setMaLichTrinh(Integer maLichTrinh) {
		this.maLichTrinh = maLichTrinh;
	}

	public Tour getTour() {
		return tour;
	}

	public void setTour(Tour tour) {
		this.tour = tour;
	}

	public Integer getThuTuNgay() {
		return thuTuNgay;
	}

	public void setThuTuNgay(Integer thuTuNgay) {
		this.thuTuNgay = thuTuNgay;
	}

	public String getHoatDong() {
		return hoatDong;
	}

	public void setHoatDong(String hoatDong) {
		this.hoatDong = hoatDong;
	}

	public DiaDiem getDiaDiem() {
		return diaDiem;
	}

	public void setDiaDiem(DiaDiem diaDiem) {
		this.diaDiem = diaDiem;
	}

    // Getters and setters
    
    
}
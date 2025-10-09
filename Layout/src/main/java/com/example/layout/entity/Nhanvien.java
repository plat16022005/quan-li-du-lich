package com.example.layout.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;


@Entity
@Table (name = "NhanVien")
public class Nhanvien {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "MaNhanVien")    
    private Integer maNhanVien;

    @OneToOne
    @JoinColumn(name = "MaTaiKhoan", referencedColumnName = "MaTaiKhoan")
    private User taiKhoan;

    @Column(name = "ChucVu", length = 50)
    private String chucVu;

    @Column(name = "LuongCoBan", precision = 18, scale = 2)
    private BigDecimal luongCoBan;

    @Column(name = "NgayVaoLam")
    @Temporal(TemporalType.DATE)
    private LocalDate ngayVaoLam;

	public Integer getMaNhanVien() {
		return maNhanVien;
	}

	public void setMaNhanVien(Integer maNhanVien) {
		this.maNhanVien = maNhanVien;
	}

	public User getTaiKhoan() {
		return taiKhoan;
	}

	public void setTaiKhoan(User taiKhoan) {
		this.taiKhoan = taiKhoan;
	}

	public String getChucVu() {
		return chucVu;
	}

	public void setChucVu(String chucVu) {
		this.chucVu = chucVu;
	}

	public BigDecimal getLuongCoBan() {
		return luongCoBan;
	}

	public void setLuongCoBan(BigDecimal luongCoBan) {
		this.luongCoBan = luongCoBan;
	}

	public LocalDate getNgayVaoLam() {
		return ngayVaoLam;
	}

	public void setNgayVaoLam(LocalDate ngayVaoLam) {
		this.ngayVaoLam = ngayVaoLam;
	}

    // Getters and setters


}

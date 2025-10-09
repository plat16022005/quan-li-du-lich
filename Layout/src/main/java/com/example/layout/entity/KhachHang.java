package com.example.layout.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "KhachHang")
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKhachHang")
    private Integer maKhachHang;

    @OneToOne
    @JoinColumn(name = "MaTaiKhoan", referencedColumnName = "MaTaiKhoan")
    private User taiKhoan;

    @Column(name = "DiaChi", length = 200)
    private String diaChi;

    @Column(name = "NgaySinh")
    @Temporal(TemporalType.DATE)
    private Date ngaySinh;

    @Column(name = "GioiTinh", length = 10)
    private String gioiTinh;

	public Integer getMaKhachHang() {
		return maKhachHang;
	}

	public void setMaKhachHang(Integer maKhachHang) {
		this.maKhachHang = maKhachHang;
	}

	public User getTaiKhoan() {
		return taiKhoan;
	}

	public void setTaiKhoan(User taiKhoan) {
		this.taiKhoan = taiKhoan;
	}

	public String getDiaChi() {
		return diaChi;
	}

	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}

	public Date getNgaySinh() {
		return ngaySinh;
	}

	public void setNgaySinh(Date ngaySinh) {
		this.ngaySinh = ngaySinh;
	}

	public String getGioiTinh() {
		return gioiTinh;
	}

	public void setGioiTinh(String gioiTinh) {
		this.gioiTinh = gioiTinh;
	}

    // Getters and setters


}
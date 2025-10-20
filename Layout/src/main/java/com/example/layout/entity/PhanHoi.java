package com.example.layout.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "PhanHoi")
public class PhanHoi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaPhanHoi")
    private Integer maPhanHoi;

    @ManyToOne
    @JoinColumn(name = "MaKhachHang")
    private KhachHang khachHang;

    public ChuyenDuLich getChuyenDuLich() {
		return chuyenDuLich;
	}

	public void setChuyenDuLich(ChuyenDuLich chuyenDuLich) {
		this.chuyenDuLich = chuyenDuLich;
	}

	@ManyToOne
    @JoinColumn(name = "MaChuyen")
    private ChuyenDuLich chuyenDuLich;

    @Column(name = "NoiDung")
    private String noiDung;

    @Column(name = "DanhGia")
    private Integer danhGia;

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao;

	public Integer getMaPhanHoi() {
		return maPhanHoi;
	}

	public void setMaPhanHoi(Integer maPhanHoi) {
		this.maPhanHoi = maPhanHoi;
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public String getNoiDung() {
		return noiDung;
	}

	public void setNoiDung(String noiDung) {
		this.noiDung = noiDung;
	}

	public Integer getDanhGia() {
		return danhGia;
	}

	public void setDanhGia(Integer danhGia) {
		this.danhGia = danhGia;
	}

	public LocalDateTime getNgayTao() {
		return ngayTao;
	}

	public void setNgayTao(LocalDateTime ngayTao) {
		this.ngayTao = ngayTao;
	}
    
    
}

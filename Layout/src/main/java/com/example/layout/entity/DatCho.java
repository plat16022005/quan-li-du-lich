package com.example.layout.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "DatCho")
public class DatCho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDatCho")
    private Integer maDatCho;

    @ManyToOne
    @JoinColumn(name = "MaKhachHang")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "MaChuyen")
    private ChuyenDuLich chuyenDuLich;

    @Column(name = "NgayDat")
    private LocalDate ngayDat;

    @Column(name = "TrangThai", length = 20)
    private String trangThai;
    
    public BigDecimal getTiLeGiam() {
		return tiLeGiam;
	}

	public void setTiLeGiam(BigDecimal tiLeGiam) {
		this.tiLeGiam = tiLeGiam;
	}

	@Column(name = "TiLeGiam", precision = 5, scale = 2)
    private BigDecimal tiLeGiam;

	@OneToMany(mappedBy = "datCho", fetch = FetchType.EAGER)
    private Set<ChiTietDatCho> chiTietDatChos;

    @ManyToMany
    @JoinTable(
        name = "DatCho_KhuyenMai",
        joinColumns = @JoinColumn(name = "MaDatCho"),
        inverseJoinColumns = @JoinColumn(name = "MaKM")
    )
    private Set<KhuyenMai> khuyenMais;

	public Integer getMaDatCho() {
		return maDatCho;
	}

	public void setMaDatCho(Integer maDatCho) {
		this.maDatCho = maDatCho;
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public ChuyenDuLich getChuyenDuLich() {
		return chuyenDuLich;
	}

	public void setChuyenDuLich(ChuyenDuLich chuyenDuLich) {
		this.chuyenDuLich = chuyenDuLich;
	}

	public LocalDate getNgayDat() {
		return ngayDat;
	}

	public void setNgayDat(LocalDate ngayDat) {
		this.ngayDat = ngayDat;
	}

	public String getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}

	public Set<ChiTietDatCho> getChiTietDatChos() {
		return chiTietDatChos;
	}

	public void setChiTietDatChos(Set<ChiTietDatCho> chiTietDatChos) {
		this.chiTietDatChos = chiTietDatChos;
	}

	public Set<KhuyenMai> getKhuyenMais() {
		return khuyenMais;
	}

	public void setKhuyenMais(Set<KhuyenMai> khuyenMais) {
		this.khuyenMais = khuyenMais;
	}
    
    // Getters and setters
    
}

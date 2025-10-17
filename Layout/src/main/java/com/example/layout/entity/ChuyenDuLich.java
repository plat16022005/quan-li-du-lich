package com.example.layout.entity;

//import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
//import java.util.Set;

@Entity
@Table(name = "ChuyenDuLich")
public class ChuyenDuLich {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaChuyen")
    private Integer maChuyen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaTour")
    private Tour tour;

    @Column(name = "NgayBatDau")
    @Temporal(TemporalType.DATE)
    private LocalDate ngayBatDau;

    @Column(name = "NgayKetThuc")
    @Temporal(TemporalType.DATE)
    private LocalDate ngayKetThuc;

    @Column(name = "TrangThai", length = 20)
    private String trangThai;

    @Column(name = "SoLuongToiDa")
    private int soLuongToiDa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaHuongDanVien")
    private Nhanvien huongDanVien;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaTaiXe")
    private Nhanvien taiXe;

	@Column(name = "GiaThueHDV", precision = 18, scale = 2)
    private BigDecimal giaThueHDV;
	@Column(name = "GiaThueTX", precision = 18, scale = 2)
    private BigDecimal giaThueTX;
	@Transient // Báo cho JPA biết không cần map cột này trong DB
    private int soLuongHienTai;
//    @ManyToMany
//    @JoinTable(
//        name = "Chuyen_KhachSan",
//        joinColumns = @JoinColumn(name = "MaChuyen"),
//        inverseJoinColumns = @JoinColumn(name = "MaKhachSan")
//    )
//    @JsonIgnore  // 🛑 Tránh lazy load khi trả JSON
//    private Set<KhachSan> khachSans;
//
//    @ManyToMany
//    @JoinTable(
//        name = "Chuyen_PhuongTien",
//        joinColumns = @JoinColumn(name = "MaChuyen"),
//        inverseJoinColumns = @JoinColumn(name = "MaPhuongTien")
//    )
//    @JsonIgnore  // 🛑 Tránh lazy load khi trả JSON
//    private Set<PhuongTien> phuongTiens;
    
    // ===================== GETTERS & SETTERS =====================

    public Integer getMaChuyen() {
        return maChuyen;
    }

    public void setMaChuyen(Integer maChuyen) {
        this.maChuyen = maChuyen;
    }

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
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

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public int getSoLuongToiDa() {
        return soLuongToiDa;
    }

    public void setSoLuongToiDa(int soLuongToiDa) {
        this.soLuongToiDa = soLuongToiDa;
    }

    public Nhanvien getHuongDanVien() {
        return huongDanVien;
    }

    public void setHuongDanVien(Nhanvien huongDanVien) {
        this.huongDanVien = huongDanVien;
    }

    public Nhanvien getTaiXe() {
        return taiXe;
    }

    public void setTaiXe(Nhanvien taiXe) {
        this.taiXe = taiXe;
    }
	public BigDecimal getGiaThueHDV() {
		return giaThueHDV;
	}

	public void setGiaThueHDV(BigDecimal giaThueHDV) {
		this.giaThueHDV = giaThueHDV;
	}

	public BigDecimal getGiaThueTX() {
		return giaThueTX;
	}

	public void setGiaThueTX(BigDecimal giaThueTX) {
		this.giaThueTX = giaThueTX;
	}
//    public Set<KhachSan> getKhachSans() {
//        return khachSans;
//    }
//
//    public void setKhachSans(Set<KhachSan> khachSans) {
//        this.khachSans = khachSans;
//    }
//
//    public Set<PhuongTien> getPhuongTiens() {
//        return phuongTiens;
//    }
//
//    public void setPhuongTiens(Set<PhuongTien> phuongTiens) {
//        this.phuongTiens = phuongTiens;
//    }
	public int getSoLuongHienTai() { return soLuongHienTai; }
    public void setSoLuongHienTai(int soLuongHienTai) { this.soLuongHienTai = soLuongHienTai; }
}

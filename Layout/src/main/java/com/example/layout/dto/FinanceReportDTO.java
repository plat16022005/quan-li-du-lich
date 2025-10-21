package com.example.layout.dto;

import java.math.BigDecimal;

public class FinanceReportDTO {

    private Integer maChuyen;
    private String tenTour;

    private BigDecimal tongDoanhThu;
    private BigDecimal tongChiPhi;
    private BigDecimal loiNhuan;

    private BigDecimal chiPhiHDV;
    private BigDecimal chiPhiTX;
    private BigDecimal chiPhiKhachSan;
    private BigDecimal chiPhiPhuongTien;

    // ✅ Constructors
    public FinanceReportDTO() {}

    public FinanceReportDTO(Integer maChuyen, String tenTour,
                            BigDecimal tongDoanhThu, BigDecimal tongChiPhi, BigDecimal loiNhuan,
                            BigDecimal chiPhiHDV, BigDecimal chiPhiTX,
                            BigDecimal chiPhiKhachSan, BigDecimal chiPhiPhuongTien) {
        this.maChuyen = maChuyen;
        this.tenTour = tenTour;
        this.tongDoanhThu = tongDoanhThu;
        this.tongChiPhi = tongChiPhi;
        this.loiNhuan = loiNhuan;
        this.chiPhiHDV = chiPhiHDV;
        this.chiPhiTX = chiPhiTX;
        this.chiPhiKhachSan = chiPhiKhachSan;
        this.chiPhiPhuongTien = chiPhiPhuongTien;
    }

    // ✅ Getters và Setters
    public Integer getMaChuyen() { return maChuyen; }
    public void setMaChuyen(Integer maChuyen) { this.maChuyen = maChuyen; }

    public String getTenTour() { return tenTour; }
    public void setTenTour(String tenTour) { this.tenTour = tenTour; }

    public BigDecimal getTongDoanhThu() { return tongDoanhThu; }
    public void setTongDoanhThu(BigDecimal tongDoanhThu) { this.tongDoanhThu = tongDoanhThu; }

    public BigDecimal getTongChiPhi() { return tongChiPhi; }
    public void setTongChiPhi(BigDecimal tongChiPhi) { this.tongChiPhi = tongChiPhi; }

    public BigDecimal getLoiNhuan() { return loiNhuan; }
    public void setLoiNhuan(BigDecimal loiNhuan) { this.loiNhuan = loiNhuan; }

    public BigDecimal getChiPhiHDV() { return chiPhiHDV; }
    public void setChiPhiHDV(BigDecimal chiPhiHDV) { this.chiPhiHDV = chiPhiHDV; }

    public BigDecimal getChiPhiTX() { return chiPhiTX; }
    public void setChiPhiTX(BigDecimal chiPhiTX) { this.chiPhiTX = chiPhiTX; }

    public BigDecimal getChiPhiKhachSan() { return chiPhiKhachSan; }
    public void setChiPhiKhachSan(BigDecimal chiPhiKhachSan) { this.chiPhiKhachSan = chiPhiKhachSan; }

    public BigDecimal getChiPhiPhuongTien() { return chiPhiPhuongTien; }
    public void setChiPhiPhuongTien(BigDecimal chiPhiPhuongTien) { this.chiPhiPhuongTien = chiPhiPhuongTien; }
}

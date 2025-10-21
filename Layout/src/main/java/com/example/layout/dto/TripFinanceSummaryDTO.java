package com.example.layout.dto;

import java.math.BigDecimal;

public class TripFinanceSummaryDTO {
    public Integer getMaChuyen() {
		return maChuyen;
	}

	public void setMaChuyen(Integer maChuyen) {
		this.maChuyen = maChuyen;
	}

	public String getTenTour() {
		return tenTour;
	}

	public void setTenTour(String tenTour) {
		this.tenTour = tenTour;
	}

	public BigDecimal getChiPhiHDV() {
		return chiPhiHDV;
	}

	public void setChiPhiHDV(BigDecimal chiPhiHDV) {
		this.chiPhiHDV = chiPhiHDV;
	}

	public BigDecimal getChiPhiTX() {
		return chiPhiTX;
	}

	public void setChiPhiTX(BigDecimal chiPhiTX) {
		this.chiPhiTX = chiPhiTX;
	}

	public BigDecimal getTongKS() {
		return tongKS;
	}

	public void setTongKS(BigDecimal tongKS) {
		this.tongKS = tongKS;
	}

	public BigDecimal getTongPT() {
		return tongPT;
	}

	public void setTongPT(BigDecimal tongPT) {
		this.tongPT = tongPT;
	}

	public BigDecimal getTongChiPhi() {
		return tongChiPhi;
	}

	public void setTongChiPhi(BigDecimal tongChiPhi) {
		this.tongChiPhi = tongChiPhi;
	}

	private Integer maChuyen;
    private String tenTour;
    private BigDecimal chiPhiHDV;
    private BigDecimal chiPhiTX;
    private BigDecimal tongKS;
    private BigDecimal tongPT;
    private BigDecimal tongChiPhi;

    public TripFinanceSummaryDTO(Integer maChuyen, String tenTour, BigDecimal chiPhiHDV, BigDecimal chiPhiTX,
                                 BigDecimal tongKS, BigDecimal tongPT, BigDecimal tongChiPhi) {
        this.maChuyen = maChuyen;
        this.tenTour = tenTour;
        this.chiPhiHDV = chiPhiHDV;
        this.chiPhiTX = chiPhiTX;
        this.tongKS = tongKS;
        this.tongPT = tongPT;
        this.tongChiPhi = tongChiPhi;
    }

    // getters/setters ...
}


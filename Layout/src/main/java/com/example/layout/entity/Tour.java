package com.example.layout.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
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

    @Column(name = "HinhAnh", length = 500)
    private String hinhAnh;

    @ManyToMany
    @JoinTable(
        name = "Tour_DiaDiem",
        joinColumns = @JoinColumn(name = "MaTour"),
        inverseJoinColumns = @JoinColumn(name = "MaDiaDiem")
    )
    @JsonIgnore  // 🛑 Thêm ở đây để bỏ qua khi serialize JSON
    private Set<DiaDiem> diaDiems;

    @OneToMany(mappedBy = "tour", fetch = FetchType.EAGER)
    @JsonIgnore  // 🛑 Thêm ở đây để bỏ qua danh sách chuyến khi serialize JSON
    private List<ChuyenDuLich> danhSachChuyen;

	@OneToMany(mappedBy = "tour", fetch = FetchType.EAGER)
    private List<LichTrinh> lichTrinhs;
	
    public List<ChuyenDuLich> getDanhSachChuyen() {
        return danhSachChuyen;
    }

    public void setDanhSachChuyen(List<ChuyenDuLich> danhSachChuyen) {
        this.danhSachChuyen = danhSachChuyen;
    }

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

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }
    public List<LichTrinh> getLichTrinhs() {
		return lichTrinhs;
	}

	public void setLichTrinhs(List<LichTrinh> lichTrinhs) {
		this.lichTrinhs = lichTrinhs;
	}

}

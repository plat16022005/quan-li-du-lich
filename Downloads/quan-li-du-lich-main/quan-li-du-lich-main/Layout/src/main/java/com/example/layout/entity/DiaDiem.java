package com.example.layout.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "DiaDiem")
public class DiaDiem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDiaDiem")
    private Integer maDiaDiem;

    @Column(name = "TenDiaDiem", length = 200, nullable = false)
    private String tenDiaDiem;

    @Column(name = "DiaChi", length = 300)
    private String diaChi;

    // Getters & Setters
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
}

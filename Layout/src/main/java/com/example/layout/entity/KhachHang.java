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

    // Getters and setters


}
package com.example.layout.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "KhuyenMai")
public class KhuyenMai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaKM")
    private Integer maKM;

    @Column(name = "TenKM", length = 100)
    private String tenKM;

    @Column(name = "TiLeGiam", precision = 5, scale = 2)
    private BigDecimal tiLeGiam;

    @Column(name = "NgayBatDau")
    @Temporal(TemporalType.DATE)
    private Date ngayBatDau;

    @Column(name = "NgayKetThuc")
    @Temporal(TemporalType.DATE)
    private Date ngayKetThuc;

    @Column(name = "MoTa", length = 500)
    private String moTa;

    @ManyToMany(mappedBy = "khuyenMais")
    private Set<DatCho> datChos;

    // Getters and setters
}
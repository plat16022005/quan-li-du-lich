package com.example.layout.entity;

import jakarta.persistence.*;
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
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayDat;

    @Column(name = "TrangThai", length = 20)
    private String trangThai;

    @OneToMany(mappedBy = "datCho")
    private Set<ChiTietDatCho> chiTietDatChos;

    @ManyToMany
    @JoinTable(
        name = "DatCho_KhuyenMai",
        joinColumns = @JoinColumn(name = "MaDatCho"),
        inverseJoinColumns = @JoinColumn(name = "MaKM")
    )
    private Set<KhuyenMai> khuyenMais;
    
    // Getters and setters
}
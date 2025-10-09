package com.example.layout.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;


@Entity
@Table (name = "NhanVien")
public class Nhanvien {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "MaNhanVien")    
    private Integer maNhanVien;

    @OneToOne
    @JoinColumn(name = "MaTaiKhoan", referencedColumnName = "MaTaiKhoan")
    private User taiKhoan;

    @Column(name = "ChucVu", length = 50)
    private String chucVu;

    @Column(name = "LuongCoBan", precision = 18, scale = 2)
    private BigDecimal luongCoBan;

    @Column(name = "NgayVaoLam")
    @Temporal(TemporalType.DATE)
    private LocalDate ngayVaoLam;

    // Getters and setters


}

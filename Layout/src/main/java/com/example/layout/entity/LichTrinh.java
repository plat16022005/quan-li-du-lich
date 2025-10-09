package com.example.layout.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "LichTrinh")
public class LichTrinh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaLichTrinh")
    private Integer maLichTrinh;

    @ManyToOne
    @JoinColumn(name = "MaTour")
    private Tour tour;

    @Column(name = "ThuTuNgay")
    private Integer thuTuNgay;

    @Column(name = "HoatDong", length = 500)
    private String hoatDong;

    @ManyToOne
    @JoinColumn(name = "MaDiaDiem")
    private DiaDiem diaDiem;

    // Getters and setters
}
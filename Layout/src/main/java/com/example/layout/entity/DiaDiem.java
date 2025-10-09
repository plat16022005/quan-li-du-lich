package com.example.layout.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "DiaDiem")
public class DiaDiem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDiaDiem")
    private Integer maDiaDiem;

    @Column(name = "TenDiaDiem", length = 200)
    private String tenDiaDiem;

    @Column(name = "DiaChi", length = 300)
    private String diaChi;

    @ManyToMany(mappedBy = "diaDiems")
    private Set<Tour> tours;

    // Getters and setters
}
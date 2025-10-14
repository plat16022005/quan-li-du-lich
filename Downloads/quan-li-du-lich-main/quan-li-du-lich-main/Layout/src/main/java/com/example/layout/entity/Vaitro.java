package com.example.layout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.Set;

@Entity
@Table(name = "VaiTro")
public class Vaitro {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "MaVaiTro")
    private Integer maVaiTro;
    
    @Column(name = "TenVaiTro", nullable = false, length = 50)
    private String tenVaiTro;

    @OneToMany(mappedBy = "maVaiTro")
    private Set<User> users;

	public Integer getMaVaiTro() {
		return maVaiTro;
	}

	public void setMaVaiTro(Integer maVaiTro) {
		this.maVaiTro = maVaiTro;
	}

	public String getTenVaiTro() {
		return tenVaiTro;
	}

	public void setTenVaiTro(String tenVaiTro) {
		this.tenVaiTro = tenVaiTro;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

    // Getters and setters

}

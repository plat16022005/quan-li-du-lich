package com.example.layout.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.layout.entity.Vaitro;

public interface VaiTroRepository extends JpaRepository<Vaitro, Integer> { 
    Optional<Vaitro> findByTenVaiTro(String tenVaiTro);
}

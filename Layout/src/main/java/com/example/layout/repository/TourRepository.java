package com.example.layout.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.layout.entity.Tour;

@Repository
public interface TourRepository extends JpaRepository<Tour, Integer> {
}

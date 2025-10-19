package com.example.layout.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.layout.entity.Tour;

@Repository
public interface TourRepository extends JpaRepository<Tour, Integer> {
	@Query("SELECT t FROM Tour t LEFT JOIN FETCH t.lichTrinhs WHERE t.maTour = :id")
	Optional<Tour> findTourWithLichTrinhs(@Param("id") Integer id);

}

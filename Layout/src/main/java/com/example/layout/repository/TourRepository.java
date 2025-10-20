package com.example.layout.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.layout.entity.Tour;

@Repository
public interface TourRepository extends JpaRepository<Tour, Integer> {
	@Query("SELECT t FROM Tour t LEFT JOIN FETCH t.lichTrinhs WHERE t.maTour = :id")
	Optional<Tour> findTourWithLichTrinhs(@Param("id") Integer id);
	@Query("""
		    SELECT DISTINCT t FROM Tour t 
		    JOIN t.danhSachChuyen c
		    WHERE (
		            :destination IS NULL 
		            OR t.moTa LIKE %:destination% 
		            OR t.tenTour LIKE %:destination%
		          )
		      AND (:minPrice IS NULL OR t.giaCoBan >= :minPrice)
		      AND (:maxPrice IS NULL OR t.giaCoBan <= :maxPrice)
		      AND (:startDate IS NULL OR c.ngayBatDau >= :startDate)
		      AND (:endDate IS NULL OR c.ngayBatDau <= :endDate)
		""")
		Page<Tour> findToursByFilters(
		        @Param("destination") String destination,
		        @Param("minPrice") java.math.BigDecimal minPrice,
		        @Param("maxPrice") java.math.BigDecimal maxPrice,
		        @Param("startDate") java.time.LocalDate startDate,
		        @Param("endDate") java.time.LocalDate endDate,
		        Pageable pageable
		);

}

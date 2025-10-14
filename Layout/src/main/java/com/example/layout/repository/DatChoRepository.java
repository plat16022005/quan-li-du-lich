package com.example.layout.repository;

import com.example.layout.entity.DatCho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatChoRepository extends JpaRepository<DatCho, Integer> {

    @Query("SELECT dc FROM DatCho dc JOIN dc.chuyenDuLich c WHERE c.tour.maTour = :maTour")
    List<DatCho> findByTourMaTour(@Param("maTour") Integer maTour);
}

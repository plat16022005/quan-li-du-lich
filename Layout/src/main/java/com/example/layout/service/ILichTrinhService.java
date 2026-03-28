package com.example.layout.service;

import com.example.layout.entity.LichTrinh;

import java.util.List;
import java.util.Optional;

public interface ILichTrinhService {
    List<LichTrinh> getLichTrinhByTourOrderByNgay(Integer maTour);
    List<LichTrinh> getLichTrinhByTour(Integer maTour);
    Optional<LichTrinh> findById(Integer maLichTrinh);
    LichTrinh saveLichTrinh(LichTrinh lt);
    LichTrinh update(Integer maLichTrinh, LichTrinh updatedData);
    void deleteLichTrinh(Integer maLichTrinh);
    List<LichTrinh> saveAll(List<LichTrinh> lichTrinhList);
    void deleteById(Integer id);
}

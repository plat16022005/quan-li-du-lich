package com.example.layout.service;

import com.example.layout.dto.ThanhToanDTO;
import com.example.layout.entity.ThanhToan;

import java.util.List;

public interface IThanhToanService {
    List<ThanhToan> findByDatChoId(Integer maDatCho);
    List<ThanhToanDTO> findDtoByDatChoId(Integer maDatCho);
    ThanhToan recordPayment(ThanhToanDTO thanhToanDTO);
}

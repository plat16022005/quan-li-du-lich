package com.example.layout.service;

import com.example.layout.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReportService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    public Map<String, Long> thongKeNguonKhachHang() {
        Map<String, Long> result = new LinkedHashMap<>();
        List<Object[]> data = khachHangRepository.thongKeNguonKhachHang();

        for (Object[] row : data) {
            String nguon = (String) row[0];
            Long soLuong = (Long) row[1];
            result.put(nguon, soLuong);
        }

        return result;
    }
}

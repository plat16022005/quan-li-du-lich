package com.example.layout.service;

import com.example.layout.dto.HanhKhachDTO;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.LichTrinh;
import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.List;

public interface ITripExportPdfService {
    byte[] exportTripDetailsToPdf(ChuyenDuLich chuyen,
                                  int soLuongHanhKhach,
                                  List<HanhKhachDTO> danhSachHanhKhach,
                                  List<LichTrinh> lichTrinh) throws DocumentException, IOException;
}

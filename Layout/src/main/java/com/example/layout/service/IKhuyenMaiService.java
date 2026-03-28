package com.example.layout.service;

import com.example.layout.entity.KhuyenMai;

import java.util.List;

public interface IKhuyenMaiService {
    List<KhuyenMai> getAll();
    KhuyenMai save(KhuyenMai km);
    void deleteById(Integer id);
    List<KhuyenMai> search(String keyword);
    KhuyenMai getById(Integer id);
}

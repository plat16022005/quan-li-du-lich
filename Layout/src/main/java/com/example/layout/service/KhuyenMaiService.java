package com.example.layout.service;

import com.example.layout.entity.KhuyenMai;
import com.example.layout.repository.KhuyenMaiRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class KhuyenMaiService {
    private final KhuyenMaiRepository repository;

    public KhuyenMaiService(KhuyenMaiRepository repository) {
        this.repository = repository;
    }

    public List<KhuyenMai> getAll() {
        return repository.findAll();
    }

    public KhuyenMai save(KhuyenMai km) {
        if (repository.existsByMaCode(km.getMaCode())) {
            throw new RuntimeException("Mã code " + km.getMaCode() + " đã tồn tại!");
        }
        if(km.getNgayBatDau().isAfter(km.getNgayKetThuc())) {
            throw new RuntimeException("Ngày bắt đầu phải trước ngày kết thúc!");
        }
        return repository.save(km);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    public List<KhuyenMai> search(String keyword) {
        return repository.searchByKeyword(keyword);
    }

    public KhuyenMai getById(Integer id) {
        return repository.findById(id).orElse(null);
    }
}

package com.example.layout.service;

import com.example.layout.entity.LichTrinh;
import com.example.layout.repository.LichTrinhRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LichTrinhService {

    private final LichTrinhRepository lichTrinhRepo;

    public LichTrinhService(LichTrinhRepository lichTrinhRepo) {
        this.lichTrinhRepo = lichTrinhRepo;
    }

    public List<LichTrinh> getLichTrinhByTourOrderByNgay(Integer maTour) {
        return lichTrinhRepo.findByTour_MaTourOrderByThuTuNgayAsc(maTour);
    }

    public List<LichTrinh> getLichTrinhByTour(Integer maTour) {
        return lichTrinhRepo.findByTour_MaTour(maTour);
    }

    public Optional<LichTrinh> findById(Integer maLichTrinh) {
        return lichTrinhRepo.findById(maLichTrinh);
    }

    public LichTrinh saveLichTrinh(LichTrinh lt) {
        lt.setMaLichTrinh(null); // Đảm bảo maLichTrinh là null để thực hiện INSERT
        return lichTrinhRepo.save(lt);
    }

    public LichTrinh update(Integer maLichTrinh, LichTrinh updatedData) {
        // Tìm đối tượng cũ
        return lichTrinhRepo.findById(maLichTrinh)
                .map(existingLichTrinh -> {
                    // Cập nhật thông tin từ dữ liệu mới
                    existingLichTrinh.setThuTuNgay(updatedData.getThuTuNgay());
                    existingLichTrinh.setHoatDong(updatedData.getHoatDong());
                    existingLichTrinh.setDiaDiem(updatedData.getDiaDiem());
                    existingLichTrinh.setKhachSan(updatedData.getKhachSan());
                    existingLichTrinh.setPhuongTien(updatedData.getPhuongTien());
                    
                    // Gọi save của repository để thực hiện UPDATE
                    return lichTrinhRepo.save(existingLichTrinh);
                })
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mục lịch trình với ID: " + maLichTrinh));
    }

    public void deleteLichTrinh(Integer maLichTrinh) {
        lichTrinhRepo.deleteById(maLichTrinh);
    }

    public List<LichTrinh> saveAll(List<LichTrinh> lichTrinhList) {
        return lichTrinhRepo.saveAll(lichTrinhList);
    }
}

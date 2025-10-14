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

    public List<LichTrinh> getLichTrinhByTour(Integer maTour) {
        return lichTrinhRepo.findByTour_MaTour(maTour);
    }

    public Optional<LichTrinh> findById(Integer maLichTrinh) {
        return lichTrinhRepo.findById(maLichTrinh);
    }

    public LichTrinh saveLichTrinh(LichTrinh lt) {
        return lichTrinhRepo.save(lt);
    }

    public void deleteLichTrinh(Integer maLichTrinh) {
        lichTrinhRepo.deleteById(maLichTrinh);
    }

    public List<LichTrinh> saveAll(List<LichTrinh> lichTrinhList) {
        return lichTrinhRepo.saveAll(lichTrinhList);
    }
}

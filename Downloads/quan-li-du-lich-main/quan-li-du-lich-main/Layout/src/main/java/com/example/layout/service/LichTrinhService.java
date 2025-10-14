package com.example.layout.service;

import com.example.layout.entity.LichTrinh;
import com.example.layout.repository.LichTrinhRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LichTrinhService {

    private final LichTrinhRepository lichTrinhRepo;

    public LichTrinhService(LichTrinhRepository lichTrinhRepo) {
        this.lichTrinhRepo = lichTrinhRepo;
    }

    public List<LichTrinh> getLichTrinhByTour(Integer maTour) {
        return lichTrinhRepo.findByTour_MaTour(maTour);
    }

    public void saveLichTrinh(LichTrinh lt) {
        lichTrinhRepo.save(lt);
    }

    public void deleteLichTrinh(Integer maLichTrinh) {
        lichTrinhRepo.deleteById(maLichTrinh);
    }
}

package com.example.layout.service;

import com.example.layout.entity.DiaDiem;
import com.example.layout.entity.KhachSan;
import com.example.layout.entity.LichTrinh;
import com.example.layout.entity.PhuongTien;
import com.example.layout.entity.Tour;
import com.example.layout.repository.DiaDiemRepository;
import com.example.layout.repository.KhachSanRepository;
import com.example.layout.repository.LichTrinhRepository;
import com.example.layout.repository.PhuongTienRepository;
import com.example.layout.repository.TourRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LichTrinhService implements ILichTrinhService {

    private final LichTrinhRepository lichTrinhRepo;
    private final TourRepository tourRepository;
    private final DiaDiemRepository diaDiemRepository;
    private final KhachSanRepository khachSanRepository;
    private final PhuongTienRepository phuongTienRepository;

    public LichTrinhService(LichTrinhRepository lichTrinhRepo,
                            TourRepository tourRepository,
                            DiaDiemRepository diaDiemRepository,
                            KhachSanRepository khachSanRepository,
                            PhuongTienRepository phuongTienRepository) {
        this.lichTrinhRepo = lichTrinhRepo;
        this.tourRepository = tourRepository;
        this.diaDiemRepository = diaDiemRepository;
        this.khachSanRepository = khachSanRepository;
        this.phuongTienRepository = phuongTienRepository;
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
    public void deleteById(Integer id) {
        lichTrinhRepo.deleteById(id);
    }

    /**
     * Thêm nhiều lịch trình cho một tour.
     * Logic được chuyển từ controller vào đây để tuân thủ SRP:
     * controller không nên biết cách truy vấn các entity liên quan.
     */
    @Override
    public void addMultiple(Integer maTour, List<ILichTrinhService.LichTrinhRequest> requests) {
        Tour tour = tourRepository.findById(maTour)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tour với mã: " + maTour));

        for (ILichTrinhService.LichTrinhRequest req : requests) {
            DiaDiem diaDiem = (req.getMaDiaDiem() != null)
                    ? diaDiemRepository.findById(req.getMaDiaDiem()).orElse(null)
                    : null;

            PhuongTien phuongTien = (req.getMaPhuongTien() != null)
                    ? phuongTienRepository.findById(req.getMaPhuongTien()).orElse(null)
                    : null;

            KhachSan khachSan = (req.getMaKhachSan() != null)
                    ? khachSanRepository.findById(req.getMaKhachSan()).orElse(null)
                    : null;

            LichTrinh lt = new LichTrinh();
            lt.setTour(tour);
            lt.setThuTuNgay(req.getThuTuNgay());
            lt.setHoatDong(req.getHoatDong());
            lt.setDiaDiem(diaDiem);
            lt.setPhuongTien(phuongTien);
            lt.setKhachSan(khachSan);

            lichTrinhRepo.save(lt);
        }
    }
}

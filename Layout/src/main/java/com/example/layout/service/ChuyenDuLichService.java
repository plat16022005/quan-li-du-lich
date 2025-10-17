package com.example.layout.service;

import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.Nhanvien;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.NhanvienRepository;

import org.springframework.data.domain.*;
import com.example.layout.service.NhanVienService;

//import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
//@RequiredArgsConstructor
public class ChuyenDuLichService {
    private final ChuyenDuLichRepository chuyenDuLichRepository;
    private final NhanVienService nhanVienService;
    private NhanvienRepository nhanVienRepository;

    public ChuyenDuLichService(ChuyenDuLichRepository chuyenDuLichRepository, NhanVienService nhanVienService) {
        this.chuyenDuLichRepository = chuyenDuLichRepository;
        this.nhanVienService = nhanVienService;
    }

    public Optional<ChuyenDuLich> getNearestChuyen(Integer maTour) {
        return chuyenDuLichRepository.findTopByTour_MaTourOrderByNgayBatDauAsc(maTour);
    }
    
    public int getTotalParticipants(Integer maChuyen) {
        return chuyenDuLichRepository.getTotalParticipants(maChuyen);
    }
    public ChuyenDuLich saveChuyen(ChuyenDuLich chuyen) {
        return chuyenDuLichRepository.save(chuyen);
    }
    public ChuyenDuLich getChuyenById(Integer maChuyen)
    {
        return chuyenDuLichRepository.findById(maChuyen).orElse(null);
    }

    public Page<ChuyenDuLich> findAll(Pageable pageable) {
        return chuyenDuLichRepository.findAll(pageable);
    }


    public List<ChuyenDuLich> findByTrangThai(String trangThai) {
        return chuyenDuLichRepository.findByTrangThai(trangThai);
    }


    public void deleteById(Integer maChuyen) {
        chuyenDuLichRepository.deleteById(maChuyen);
    }


    public ChuyenDuLich updateStatus(Integer maChuyen, String newStatus) {
        ChuyenDuLich chuyen = getChuyenById(maChuyen);
        if (chuyen != null) {
            chuyen.setTrangThai(newStatus);
            return chuyenDuLichRepository.save(chuyen);
        }
        throw new RuntimeException("Không tìm thấy chuyến du lịch với ID: " + maChuyen);
    }

    public ChuyenDuLich assignStaff(Integer maTaiXe, Integer maHuongDanVien, Integer maChuyen) {
        ChuyenDuLich chuyen = chuyenDuLichRepository.findById(maChuyen)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến du lịch với ID: " + maChuyen));

        Nhanvien hdv = nhanVienService.findById(maHuongDanVien)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Hướng dẫn viên với ID: " + maHuongDanVien));

        Nhanvien taixe = nhanVienService.findById(maTaiXe)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài xế với ID: " + maTaiXe));
        
        chuyen.setHuongDanVien(hdv);
        chuyen.setTaiXe(taixe);

        return chuyenDuLichRepository.save(chuyen);
    }

    public List<ChuyenDuLich> getAvailableTripsByRole(int maVaiTro) {
        if (maVaiTro == 3) { 
            return chuyenDuLichRepository.findByHuongDanVienIsNullAndTrangThai("Sắp diễn ra");
        } else if (maVaiTro == 5) {
            return chuyenDuLichRepository.findByHuongDanVienIsNullAndTrangThai("Sắp diễn ra");
        }
        return Collections.emptyList();
    }

    public List<ChuyenDuLich> getAssignedTripsByEmployee(int maNhanVien, int maVaiTro) {
        if (maVaiTro == 3) {
            return chuyenDuLichRepository.findByHuongDanVien_MaNhanVien(maNhanVien);
        } else if (maVaiTro == 5) {
            return chuyenDuLichRepository.findByHuongDanVien_MaNhanVien(maNhanVien);
        }
        return Collections.emptyList();
    }

    public void assignTripToEmployee(int maChuyen, int maNhanVien, int maVaiTro) {
        ChuyenDuLich chuyen = chuyenDuLichRepository.findById(maChuyen)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi với mã: " + maChuyen));

        Nhanvien nhanVien = nhanVienRepository.findById(maNhanVien)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với mã: " + maNhanVien));

        if (maVaiTro == 3) {
            if (chuyen.getHuongDanVien() != null) {
                throw new IllegalStateException("Chuyến đi này đã có Hướng dẫn viên nhận.");
            }
            chuyen.setHuongDanVien(nhanVien);
        } else if (maVaiTro == 5) {
            if (chuyen.getTaiXe() != null) {
                throw new IllegalStateException("Chuyến đi này đã có Tài xế nhận.");
            }
            chuyen.setTaiXe(nhanVien);
        }
        chuyenDuLichRepository.save(chuyen);
    }
    
    public Map<String, Object> getYearlyStats(int year, Integer staffId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> details = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        int totalTrips = 0;
        double totalBaseSalary = 0;
        int totalHours = 0;
        double totalBonus = 0;

        for (int m = 1; m <= 12; m++) {
            List<ChuyenDuLich> trips = chuyenDuLichRepository.findByMonthYearAndStaff(m, year, staffId);

            int monthTrips = 0;
            double monthBase = 0;
            int monthHours = 0;
            double monthBonus = 0;

            for (ChuyenDuLich t : trips) {
                if (t.getTrangThai() != null && t.getTrangThai().toLowerCase().contains("hoàn") ||
                    t.getTrangThai() != null && t.getTrangThai().toLowerCase().contains("finished")) {
                    monthTrips++;
                    // tính giờ giả định: số ngày * 8 (có thể điều chỉnh)
                    if (t.getNgayBatDau() != null && t.getNgayKetThuc() != null) {
                        long days = ChronoUnit.DAYS.between(t.getNgayBatDau(), t.getNgayKetThuc()) + 1;
                        monthHours += (int)(days * 8);
                    }
                    if (t.getHuongDanVien() != null && t.getHuongDanVien().equals(staffId) && t.getGiaThueHDV() != null) {
                        monthBase += t.getGiaThueHDV().doubleValue();
                    }
                    if (t.getTaiXe() != null && t.getTaiXe().equals(staffId) && t.getGiaThueTX() != null) {
                        monthBase += t.getGiaThueTX().doubleValue();
                    }
                    // bonus tạm: bạn có thể điều chỉnh logic tính thưởng riêng
                    monthBonus += calculateBonus(t);
                }
            }

            Map<String, Object> mdata = new HashMap<>();
            mdata.put("month", m);
            mdata.put("completedTrips", monthTrips);
            mdata.put("totalHours", monthHours);
            mdata.put("baseSalary", monthBase);
            mdata.put("bonus", monthBonus);
            mdata.put("totalSalary", monthBase + monthBonus);

            details.add(mdata);

            labels.add("T" + m);
            values.add(monthBase + monthBonus);

            totalTrips += monthTrips;
            totalBaseSalary += monthBase;
            totalHours += monthHours;
            totalBonus += monthBonus;
        }

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("values", values);

        result.put("details", details);
        result.put("chartData", chartData);
        result.put("totalTrips", totalTrips);
        result.put("totalHours", totalHours);
        result.put("totalBaseSalary", totalBaseSalary);
        result.put("totalBonus", totalBonus);
        result.put("totalSalary", totalBaseSalary + totalBonus);

        return result;
    }

    public Map<String, Object> getMonthlyStats(int year, int month, Integer staffId) {
        // Tương tự: trả về danh sách ngày trong tháng, và các chuyến ứng với ngày đó
        Map<String, Object> result = new HashMap<>();
        List<ChuyenDuLich> trips = chuyenDuLichRepository.findByMonthYearAndStaff(month, year, staffId);

        List<Map<String, Object>> details = new ArrayList<>();
        int totalTrips = 0;
        double totalBase = 0;
        double totalBonus = 0;
        int totalHours = 0;

        for (ChuyenDuLich t : trips) {
            if (t.getTrangThai() != null && (t.getTrangThai().toLowerCase().contains("hoàn") ||
                t.getTrangThai().toLowerCase().contains("finished"))) {

                Map<String, Object> r = new HashMap<>();
                r.put("maChuyen", t.getMaChuyen());
                r.put("period", t.getNgayBatDau() + " -> " + t.getNgayKetThuc());
                long days = 0;
                if (t.getNgayBatDau() != null && t.getNgayKetThuc() != null) {
                    days = ChronoUnit.DAYS.between(t.getNgayBatDau(), t.getNgayKetThuc()) + 1;
                }
                int hours = (int) (days * 8);
                r.put("hours", hours);
                double base = 0;
                if (t.getHuongDanVien() != null && t.getHuongDanVien().equals(staffId) && t.getGiaThueHDV() != null) {
                    base += t.getGiaThueHDV().doubleValue();
                }
                if (t.getTaiXe() != null && t.getTaiXe().equals(staffId) && t.getGiaThueTX() != null) {
                    base += t.getGiaThueTX().doubleValue();
                }
                double bonus = calculateBonus(t);
                r.put("baseSalary", base);
                r.put("bonus", bonus);
                r.put("totalSalary", base + bonus);

                details.add(r);

                totalTrips++;
                totalHours += hours;
                totalBase += base;
                totalBonus += bonus;
            }
        }

        result.put("details", details);
        result.put("totalTrips", totalTrips);
        result.put("totalHours", totalHours);
        result.put("totalBaseSalary", totalBase);
        result.put("totalBonus", totalBonus);
        result.put("totalSalary", totalBase + totalBonus);
        return result;
    }

    public Map<String, Object> getPeriodStats(LocalDate from, LocalDate to, Integer staffId) {
        Map<String, Object> result = new HashMap<>();
        List<ChuyenDuLich> trips = chuyenDuLichRepository.findByPeriodAndStaff(from, to, staffId);

        // Tương tự getMonthlyStats, gom theo ngày hoặc theo tháng tùy bạn muốn
        // Ở đây trả về chi tiết từng chuyến
        List<Map<String, Object>> details = new ArrayList<>();
        for (ChuyenDuLich t : trips) {
            if (t.getTrangThai() != null && (t.getTrangThai().toLowerCase().contains("hoàn") ||
                t.getTrangThai().toLowerCase().contains("finished"))) {

                Map<String, Object> r = new HashMap<>();
                r.put("maChuyen", t.getMaChuyen());
                r.put("period", t.getNgayBatDau() + " -> " + t.getNgayKetThuc());
                // compute hours, base, bonus similar as above
                long days = 0;
                if (t.getNgayBatDau() != null && t.getNgayKetThuc() != null) {
                    days = ChronoUnit.DAYS.between(t.getNgayBatDau(), t.getNgayKetThuc()) + 1;
                }
                int hours = (int) (days * 8);
                r.put("hours", hours);

                double base = 0;
                if (t.getHuongDanVien() != null && t.getHuongDanVien().equals(staffId) && t.getGiaThueHDV() != null) {
                    base += t.getGiaThueHDV().doubleValue();
                }
                if (t.getTaiXe() != null && t.getTaiXe().equals(staffId) && t.getGiaThueTX() != null) {
                    base += t.getGiaThueTX().doubleValue();
                }

                double bonus = calculateBonus(t);
                r.put("baseSalary", base);
                r.put("bonus", bonus);
                r.put("totalSalary", base + bonus);

                details.add(r);
            }
        }

        result.put("details", details);
        return result;
    }

    private double calculateBonus(ChuyenDuLich trip) {
        // Placeholder: logic tính thưởng theo tiêu chí thực tế (đánh giá, số khách, KPI,...)
        // Hiện tạm return 0
        return 0;
    }
}

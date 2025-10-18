package com.example.layout.service;

import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.Nhanvien;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.NhanvienRepository;

import org.springframework.data.domain.*;
import com.example.layout.service.NhanVienService;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ChuyenDuLichService {
    private final ChuyenDuLichRepository chuyenDuLichRepository;
    private final NhanVienService nhanVienService;
    private final NhanvienRepository nhanVienRepository;

    public ChuyenDuLichService(ChuyenDuLichRepository chuyenDuLichRepository, 
                               NhanVienService nhanVienService,
                               NhanvienRepository nhanVienRepository) {
        this.chuyenDuLichRepository = chuyenDuLichRepository;
        this.nhanVienService = nhanVienService;
        this.nhanVienRepository = nhanVienRepository;
    }

    // === CÁC PHƯƠNG THỨC CƠ BẢN ===
    
    public Optional<ChuyenDuLich> getNearestChuyen(Integer maTour) {
        return chuyenDuLichRepository.findTopByTour_MaTourOrderByNgayBatDauAsc(maTour);
    }
    
    public int getTotalParticipants(Integer maChuyen) {
        return chuyenDuLichRepository.getTotalParticipants(maChuyen);
    }
    
    public ChuyenDuLich saveChuyen(ChuyenDuLich chuyen) {
        return chuyenDuLichRepository.save(chuyen);
    }
    
    public ChuyenDuLich getChuyenById(Integer maChuyen) {
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

    public List<ChuyenDuLich> findByTour_MaTour(Integer maTour) {
        return chuyenDuLichRepository.findByTour_MaTour(maTour);
    }

    public Page<ChuyenDuLich> findByTour_MaTour(Integer maTour, Pageable pageable) {
        return chuyenDuLichRepository.findByTour_MaTour(maTour, pageable);
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
        if (maVaiTro == 3 || maVaiTro == 5) { 
            return chuyenDuLichRepository.findByHuongDanVienIsNullAndTrangThai("Sắp diễn ra");
        }
        return Collections.emptyList();
    }

    public List<ChuyenDuLich> getAssignedTripsByEmployee(int maNhanVien, int maVaiTro) {
        if (maVaiTro == 3 || maVaiTro == 5) {
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
    
    // === THỐNG KÊ THÙ LAO (LƯƠNG CƠ BẢN + TIỀN THUÊ) ===
    
    /**
     * Thống kê thù lao theo năm
     * Tính: Lương cơ bản (12 tháng) + Tiền thuê các chuyến đi hoàn thành
     */
    public Map<String, Object> getYearlyStats(int year, Integer staffId) {
        Map<String, Object> result = new HashMap<>();
        
        // Lấy thông tin nhân viên
        Nhanvien nhanVien = nhanVienRepository.findById(staffId).orElse(null);
        BigDecimal luongCoBanThang = BigDecimal.ZERO;
        if (nhanVien != null && nhanVien.getLuongCoBan() != null) {
            luongCoBanThang = nhanVien.getLuongCoBan();
        }
        
        List<Map<String, Object>> details = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        int totalTrips = 0;
        BigDecimal totalTripSalary = BigDecimal.ZERO;
        int totalHours = 0;
        double totalBonus = 0;

        // Duyệt qua 12 tháng
        for (int m = 1; m <= 12; m++) {
            List<ChuyenDuLich> trips = chuyenDuLichRepository.findByMonthYearAndStaff(m, year, staffId);

            int monthTrips = 0;
            BigDecimal monthTripSalary = BigDecimal.ZERO;
            int monthHours = 0;
            double monthBonus = 0;

            for (ChuyenDuLich t : trips) {
                // Kiểm tra trạng thái hoàn thành
                String status = t.getTrangThai();
                if (status != null && (status.contains("hoàn") || 
                    status.equalsIgnoreCase("Finished") || 
                    status.equalsIgnoreCase("Đã hoàn thành"))) {
                    
                    monthTrips++;
                    
                    // Tính số giờ
                    if (t.getNgayBatDau() != null && t.getNgayKetThuc() != null) {
                        long days = ChronoUnit.DAYS.between(t.getNgayBatDau(), t.getNgayKetThuc()) + 1;
                        monthHours += (int)(days * 24);
                    }
                    
                    // Tính tiền thuê chuyến đi
                    if (t.getHuongDanVien() != null && 
                        t.getHuongDanVien().getMaNhanVien().equals(staffId) && 
                        t.getGiaThueHDV() != null) {
                        monthTripSalary = monthTripSalary.add(t.getGiaThueHDV());
                    }
                    
                    if (t.getTaiXe() != null && 
                        t.getTaiXe().getMaNhanVien().equals(staffId) && 
                        t.getGiaThueTX() != null) {
                        monthTripSalary = monthTripSalary.add(t.getGiaThueTX());
                    }
                    
                    monthBonus += calculateBonus(t);
                }
            }

            // Lương tháng = Lương cơ bản + Tiền thuê các chuyến
            BigDecimal monthTotal = luongCoBanThang.add(monthTripSalary).add(BigDecimal.valueOf(monthBonus));

            Map<String, Object> mdata = new HashMap<>();
            mdata.put("month", m);
            mdata.put("completedTrips", monthTrips);
            mdata.put("totalHours", monthHours);
            mdata.put("baseSalary", luongCoBanThang);
            mdata.put("tripSalary", monthTripSalary);
            mdata.put("bonus", monthBonus);
            mdata.put("totalSalary", monthTotal);

            details.add(mdata);

            labels.add("T" + m);
            values.add(monthTotal.doubleValue());

            totalTrips += monthTrips;
            totalTripSalary = totalTripSalary.add(monthTripSalary);
            totalHours += monthHours;
            totalBonus += monthBonus;
        }

        // Tổng lương cả năm
        BigDecimal totalBaseSalary = luongCoBanThang.multiply(new BigDecimal(12));
        BigDecimal totalSalary = totalBaseSalary.add(totalTripSalary).add(BigDecimal.valueOf(totalBonus));

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("values", values);

        result.put("details", details);
        result.put("chartData", chartData);
        result.put("totalTrips", totalTrips);
        result.put("totalHours", totalHours);
        result.put("totalBaseSalary", totalBaseSalary);
        result.put("totalTripSalary", totalTripSalary);
        result.put("totalBonus", totalBonus);
        result.put("totalSalary", totalSalary);

        return result;
    }

    /**
     * Thống kê thù lao theo tháng
     * Tính: Lương cơ bản (1 tháng) + Tiền thuê các chuyến đi hoàn thành
     */
    public Map<String, Object> getMonthlyStats(int year, int month, Integer staffId) {
        Map<String, Object> result = new HashMap<>();
        
        // Lấy thông tin nhân viên
        Nhanvien nhanVien = nhanVienRepository.findById(staffId).orElse(null);
        BigDecimal luongCoBan = BigDecimal.ZERO;
        if (nhanVien != null && nhanVien.getLuongCoBan() != null) {
            luongCoBan = nhanVien.getLuongCoBan();
        }
        
        List<ChuyenDuLich> trips = chuyenDuLichRepository.findByMonthYearAndStaff(month, year, staffId);

        List<Map<String, Object>> details = new ArrayList<>();
        int totalTrips = 0;
        BigDecimal totalTripSalary = BigDecimal.ZERO;
        double totalBonus = 0;
        int totalHours = 0;

        for (ChuyenDuLich t : trips) {
            String status = t.getTrangThai();
            if (status != null && (status.contains("hoàn") || 
                status.equalsIgnoreCase("Finished") || 
                status.equalsIgnoreCase("Đã hoàn thành"))) {

                Map<String, Object> r = new HashMap<>();
                r.put("maChuyen", t.getMaChuyen());
                r.put("period", t.getNgayBatDau() + " → " + t.getNgayKetThuc());
                
                // Tính số giờ
                long days = 0;
                if (t.getNgayBatDau() != null && t.getNgayKetThuc() != null) {
                    days = ChronoUnit.DAYS.between(t.getNgayBatDau(), t.getNgayKetThuc()) + 1;
                }
                int hours = (int) (days * 24);
                r.put("hours", hours);
                
                // Tính tiền thuê chuyến
                BigDecimal tripSalary = BigDecimal.ZERO;
                if (t.getHuongDanVien() != null && 
                    t.getHuongDanVien().getMaNhanVien().equals(staffId) && 
                    t.getGiaThueHDV() != null) {
                    tripSalary = tripSalary.add(t.getGiaThueHDV());
                }
                
                if (t.getTaiXe() != null && 
                    t.getTaiXe().getMaNhanVien().equals(staffId) && 
                    t.getGiaThueTX() != null) {
                    tripSalary = tripSalary.add(t.getGiaThueTX());
                }
                
                double bonus = calculateBonus(t);
                r.put("tripSalary", tripSalary);
                r.put("bonus", bonus);
                r.put("totalSalary", tripSalary.add(BigDecimal.valueOf(bonus)));

                details.add(r);

                totalTrips++;
                totalHours += hours;
                totalTripSalary = totalTripSalary.add(tripSalary);
                totalBonus += bonus;
            }
        }

        // Tổng lương tháng = Lương cơ bản + Tiền thuê + Thưởng
        BigDecimal totalSalary = luongCoBan.add(totalTripSalary).add(BigDecimal.valueOf(totalBonus));

        result.put("details", details);
        result.put("totalTrips", totalTrips);
        result.put("totalHours", totalHours);
        result.put("totalBaseSalary", luongCoBan);
        result.put("totalTripSalary", totalTripSalary);
        result.put("totalBonus", totalBonus);
        result.put("totalSalary", totalSalary);
        
        // Chart data
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", List.of("Lương Cơ Bản", "Tiền Thuê Chuyến", "Thưởng"));
        chartData.put("values", List.of(luongCoBan, totalTripSalary, totalBonus));
        result.put("chartData", chartData);
        
        return result;
    }

    /**
     * Thống kê thù lao theo khoảng thời gian tùy chọn
     * Chỉ tính tiền thuê các chuyến đi (không tính lương cơ bản)
     */
    public Map<String, Object> getPeriodStats(LocalDate from, LocalDate to, Integer staffId) {
        Map<String, Object> result = new HashMap<>();
        
        // Lấy thông tin nhân viên
        Nhanvien nhanVien = nhanVienRepository.findById(staffId).orElse(null);
        
        List<ChuyenDuLich> trips = chuyenDuLichRepository.findByPeriodAndStaff(from, to, staffId);

        List<Map<String, Object>> details = new ArrayList<>();
        int totalTrips = 0;
        BigDecimal totalTripSalary = BigDecimal.ZERO;
        double totalBonus = 0;
        int totalHours = 0;

        for (ChuyenDuLich t : trips) {
            String status = t.getTrangThai();
            if (status != null && (status.contains("hoàn") || 
                status.equalsIgnoreCase("Finished") || 
                status.equalsIgnoreCase("Đã hoàn thành"))) {

                Map<String, Object> r = new HashMap<>();
                r.put("maChuyen", t.getMaChuyen());
                r.put("period", t.getNgayBatDau() + " → " + t.getNgayKetThuc());
                
                // Tính số giờ
                long days = 0;
                if (t.getNgayBatDau() != null && t.getNgayKetThuc() != null) {
                    days = ChronoUnit.DAYS.between(t.getNgayBatDau(), t.getNgayKetThuc()) + 1;
                }
                int hours = (int) (days * 24);
                r.put("hours", hours);

                // Tính tiền thuê chuyến
                BigDecimal tripSalary = BigDecimal.ZERO;
                if (t.getHuongDanVien() != null && 
                    t.getHuongDanVien().getMaNhanVien().equals(staffId) && 
                    t.getGiaThueHDV() != null) {
                    tripSalary = tripSalary.add(t.getGiaThueHDV());
                }
                
                if (t.getTaiXe() != null && 
                    t.getTaiXe().getMaNhanVien().equals(staffId) && 
                    t.getGiaThueTX() != null) {
                    tripSalary = tripSalary.add(t.getGiaThueTX());
                }

                double bonus = calculateBonus(t);
                r.put("tripSalary", tripSalary);
                r.put("bonus", bonus);
                r.put("totalSalary", tripSalary.add(BigDecimal.valueOf(bonus)));

                details.add(r);
                
                totalTrips++;
                totalHours += hours;
                totalTripSalary = totalTripSalary.add(tripSalary);
                totalBonus += bonus;
            }
        }

        // Tổng thu nhập trong kỳ (không tính lương cơ bản vì là khoảng thời gian tùy chọn)
        BigDecimal totalSalary = totalTripSalary.add(BigDecimal.valueOf(totalBonus));

        result.put("details", details);
        result.put("totalTrips", totalTrips);
        result.put("totalHours", totalHours);
        result.put("totalBaseSalary", BigDecimal.ZERO); // Không áp dụng cho kỳ tùy chọn
        result.put("totalTripSalary", totalTripSalary);
        result.put("totalBonus", totalBonus);
        result.put("totalSalary", totalSalary);
        
        // Chart data
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", List.of("Tiền Thuê Chuyến", "Thưởng"));
        chartData.put("values", List.of(totalTripSalary, totalBonus));
        result.put("chartData", chartData);
        
        return result;
    }

    /**
     * Tính thưởng cho chuyến đi (có thể mở rộng sau)
     */
    private double calculateBonus(ChuyenDuLich trip) {

        return 0;
    }
}
package com.example.layout.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.layout.dto.FinanceReportDTO;
import com.example.layout.dto.TripFinanceSummaryDTO;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.DatChoRepository;

@Service
public class FinanceService {

    @Autowired
    private ChuyenDuLichRepository chuyenDuLichRepository;

    @Autowired
    private DatChoRepository datChoRepository;

    public FinanceReportDTO getFinanceReport(Integer maChuyen) {
        List<Object[]> results = chuyenDuLichRepository.getFinanceData(maChuyen);

        if (results == null || results.isEmpty()) return null;
        Object[] result = results.get(0);
        // ⚡️ Rút từng cột ra đúng kiểu
        Integer idChuyen = ((Number) result[0]).intValue();
        String tenTour = (String) result[1];

        BigDecimal chiPhiHDV = new BigDecimal(result[2].toString());
        BigDecimal chiPhiTX = new BigDecimal(result[3].toString());
        BigDecimal tongKS = new BigDecimal(result[4].toString());
        BigDecimal tongPT = new BigDecimal(result[5].toString());
        BigDecimal tongChiPhi = new BigDecimal(result[6].toString());

        // 🧮 Doanh thu (tổng vé)
        BigDecimal tongDoanhThu = datChoRepository.getTotalRevenueByTrip(maChuyen);
        if (tongDoanhThu == null) tongDoanhThu = BigDecimal.ZERO;

        BigDecimal loiNhuan = tongDoanhThu.subtract(tongChiPhi);

        return new FinanceReportDTO(
                idChuyen,
                tenTour,
                tongDoanhThu,
                tongChiPhi,
                loiNhuan,
                chiPhiHDV,
                chiPhiTX,
                tongKS,
                tongPT
        );
    }
    public List<TripFinanceSummaryDTO> getAllTripsFinanceReport() {
        List<Object[]> results = chuyenDuLichRepository.getAllFinanceData();
        List<TripFinanceSummaryDTO> list = new ArrayList<>();

        for (Object[] row : results) {
        	BigDecimal chiPhiHDV = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
        	BigDecimal chiPhiTX = row[3] != null ? new BigDecimal(row[3].toString()) : BigDecimal.ZERO;
        	BigDecimal tongKS = row[4] != null ? new BigDecimal(row[4].toString()) : BigDecimal.ZERO;
        	BigDecimal tongPT = row[5] != null ? new BigDecimal(row[5].toString()) : BigDecimal.ZERO;
        	BigDecimal tongChiPhi = row[6] != null ? new BigDecimal(row[6].toString()) : BigDecimal.ZERO;

        	TripFinanceSummaryDTO dto = new TripFinanceSummaryDTO(
        	    ((Number) row[0]).intValue(),
        	    (String) row[1],
        	    chiPhiHDV,
        	    chiPhiTX,
        	    tongKS,
        	    tongPT,
        	    tongChiPhi
        	);


            list.add(dto);
        }
        return list;
    }

}

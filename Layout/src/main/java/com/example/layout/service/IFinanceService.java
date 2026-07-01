package com.example.layout.service;

import com.example.layout.dto.FinanceReportDTO;
import com.example.layout.dto.TripFinanceSummaryDTO;

import java.util.List;

public interface IFinanceService {
    FinanceReportDTO getFinanceReport(Integer maChuyen);
    List<TripFinanceSummaryDTO> getAllTripsFinanceReport();
}

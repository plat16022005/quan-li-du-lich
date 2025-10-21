package com.example.layout.controller.nhanvien;

import com.example.layout.dto.TopCustomerDTO;
import com.example.layout.dto.TripFinanceSummaryDTO;
import com.example.layout.repository.DatChoRepository;
import com.example.layout.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/nhanvien/report/api") // ✅ base URL
public class ReportApiController {

    @Autowired
    private FinanceService financeService;

    @GetMapping("/trip-expense")
    public List<TripFinanceSummaryDTO> getTripExpenseReport() {
        return financeService.getAllTripsFinanceReport();
    }
    @Autowired
    private DatChoRepository datChoRepository;

    @GetMapping("/top-customers")
    public List<TopCustomerDTO> getTopCustomers() {
        return datChoRepository.findTopCustomers();
    }
}

package com.example.layout.controller.nhanvien;

import com.example.layout.dto.TopCustomerDTO;
import com.example.layout.dto.TripFinanceSummaryDTO;
import com.example.layout.service.IDatChoService;
import com.example.layout.service.IFinanceService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/nhanvien/report/api") // ✅ base URL
public class ReportApiController {

    private final IFinanceService financeService;

    @GetMapping("/trip-expense")
    public List<TripFinanceSummaryDTO> getTripExpenseReport() {
        return financeService.getAllTripsFinanceReport();
    }
    private final IDatChoService datChoService;

    public ReportApiController(IFinanceService financeService, IDatChoService datChoService) {
        this.financeService = financeService;
        this.datChoService = datChoService;
    }


    @GetMapping("/top-customers")
    public List<TopCustomerDTO> getTopCustomers() {
        return datChoService.findTopCustomers();
    }
}

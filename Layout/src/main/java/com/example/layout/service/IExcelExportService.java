package com.example.layout.service;

import java.io.IOException;
import java.time.LocalDate;

public interface IExcelExportService {
    byte[] exportToExcel(String reportType, LocalDate fromDate, LocalDate toDate) throws IOException;
}

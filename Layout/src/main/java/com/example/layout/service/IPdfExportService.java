package com.example.layout.service;

import com.itextpdf.text.DocumentException;
import java.io.IOException;
import java.time.LocalDate;

public interface IPdfExportService {
    byte[] exportToPDF(String reportType, LocalDate fromDate, LocalDate toDate) throws IOException, DocumentException;
}

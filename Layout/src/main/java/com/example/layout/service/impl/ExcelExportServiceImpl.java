package com.example.layout.service.impl;

import com.example.layout.service.IExcelExportService;
import com.example.layout.service.IReportService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class ExcelExportServiceImpl implements IExcelExportService {

    private final IReportService reportService;

    public ExcelExportServiceImpl(IReportService reportService) {
        this.reportService = reportService;
    }

    public byte[] exportToExcel(String reportType, LocalDate fromDate, LocalDate toDate) throws IOException {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Báo cáo");

            CellStyle headerStyle = workbook.createCellStyle();
            XSSFFont headerFont = ((XSSFWorkbook) workbook).createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row headerRow = sheet.createRow(0);
            if ("marketing".equals(reportType) || "customers".equals(reportType)) {
                createMarketingExcelReport(sheet, headerRow, headerStyle);
            } else if ("bookings".equals(reportType)) {
                createBookingStatusExcelReport(sheet, headerRow, headerStyle);
            } else if ("expense".equals(reportType)) {
                createExpenseExcelReport(sheet, headerRow, headerStyle, fromDate, toDate, "Chi phí");
            } else if ("revenue".equals(reportType)) {
                createExpenseExcelReport(sheet, headerRow, headerStyle, fromDate, toDate, "Doanh thu");
            }

            for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            return createSimpleExcelFallback(reportType, fromDate, toDate);
        }
    }

    private byte[] createSimpleExcelFallback(String reportType, LocalDate fromDate, LocalDate toDate) throws IOException {
        StringBuilder csvContent = new StringBuilder();
        
        String title = "";
        if ("marketing".equals(reportType) || "customers".equals(reportType)) {
            title = "Nguồn Khách Hàng";
        } else if ("bookings".equals(reportType)) {
            title = "Trạng Thái Đặt Tour";
        } else if ("expense".equals(reportType)) {
            title = "Chi Phí";
        } else if ("revenue".equals(reportType)) {
            title = "Doanh Thu";
        }
        csvContent.append("Báo cáo ").append(title).append("\n");
        
        if ("marketing".equals(reportType) || "customers".equals(reportType)) {
            csvContent.append("Nguồn khách hàng,Số lượng khách\n");
            Map<String, Long> marketingData = reportService.thongKeNguonKhachHang();
            for (Map.Entry<String, Long> entry : marketingData.entrySet()) {
                csvContent.append(getMarketingSourceLabel(entry.getKey())).append(",").append(entry.getValue()).append("\n");
            }
        } else if ("bookings".equals(reportType)) {
            csvContent.append("Trạng thái đặt tour,Số lượng\n");
            Map<String, Long> bookingData = reportService.thongKeTrangThaiDatCho();
            for (Map.Entry<String, Long> entry : bookingData.entrySet()) {
                csvContent.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
            }
        } else if ("expense".equals(reportType)) {
            csvContent.append("Thời gian,Chi phí (VNĐ)\n");
            if (fromDate != null) {
                Map<String, BigDecimal> expenseData = reportService.thongKeChiPhi(fromDate.getYear(), fromDate.getMonthValue());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                for (Map.Entry<String, BigDecimal> entry : expenseData.entrySet()) {
                    if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                        String dateStr = LocalDate.parse(entry.getKey()).format(formatter);
                        csvContent.append(dateStr).append(",").append(entry.getValue()).append("\n");
                    }
                }
            }
        } else if ("revenue".equals(reportType)) {
            csvContent.append("Thời gian,Doanh thu (VNĐ)\n");
            if (fromDate != null) {
                Map<String, BigDecimal> revenueData = reportService.thongKeChiPhi(fromDate.getYear(), fromDate.getMonthValue()); // Should arguably be thongKeDoanhThu, but replicating the original ExportService behavior exactly
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                for (Map.Entry<String, BigDecimal> entry : revenueData.entrySet()) {
                    if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                        String dateStr = LocalDate.parse(entry.getKey()).format(formatter);
                        csvContent.append(dateStr).append(",").append(entry.getValue()).append("\n");
                    }
                }
            }
        }
        
        return csvContent.toString().getBytes("UTF-8");
    }

    private void createMarketingExcelReport(Sheet sheet, Row headerRow, CellStyle headerStyle) {
        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("Nguồn khách hàng");
        headerCell1.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("Số lượng");
        headerCell2.setCellStyle(headerStyle);

        Map<String, Long> marketingData = reportService.thongKeNguonKhachHang();
        int rowNum = 1;
        for (Map.Entry<String, Long> entry : marketingData.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(getMarketingSourceLabel(entry.getKey()));
            row.createCell(1).setCellValue(entry.getValue());
        }
    }

    private void createExpenseExcelReport(Sheet sheet, Row headerRow, CellStyle headerStyle, LocalDate fromDate, LocalDate toDate, String type) {
        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("Thời gian");
        headerCell1.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue(type + " (VNĐ)");
        headerCell2.setCellStyle(headerStyle);

        CellStyle currencyStyle = sheet.getWorkbook().createCellStyle();
        currencyStyle.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat("#,##0"));

        if (fromDate != null) {
            Map<String, BigDecimal> expenseData = reportService.thongKeChiPhi(fromDate.getYear(), fromDate.getMonthValue());
            int rowNum = 1;
            for (Map.Entry<String, BigDecimal> entry : expenseData.entrySet()) {
                if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                    Row row = sheet.createRow(rowNum++);
                    Cell dateCell = row.createCell(0);
                    dateCell.setCellValue(LocalDate.parse(entry.getKey()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    Cell amountCell = row.createCell(1);
                    amountCell.setCellValue(entry.getValue().doubleValue());
                    amountCell.setCellStyle(currencyStyle);
                }
            }
        }
    }

    private void createBookingStatusExcelReport(Sheet sheet, Row headerRow, CellStyle headerStyle) {
        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("Trạng thái đặt tour");
        headerCell1.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("Số lượng");
        headerCell2.setCellStyle(headerStyle);

        Map<String, Long> bookingData = reportService.thongKeTrangThaiDatCho();
        int rowNum = 1;
        for (Map.Entry<String, Long> entry : bookingData.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }
    }

    private String getMarketingSourceLabel(String key) {
        return switch (key) {
            case "friend" -> "Người quen giới thiệu";
            case "facebook" -> "Facebook";
            case "tiktok" -> "TikTok";
            case "google" -> "Tìm kiếm trên Google";
            case "youtube" -> "Quảng cáo YouTube";
            case "website" -> "Website/Blog khác";
            default -> "Khác";
        };
    }
}

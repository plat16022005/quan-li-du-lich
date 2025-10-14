package com.example.layout.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.PageSize;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;

@Service
public class ExportService {

    @Autowired
    private ReportService reportService;

    private static final Locale VIETNAM_LOCALE = Locale.forLanguageTag("vi-VN");

    public byte[] exportToExcel(String reportType, LocalDate fromDate, LocalDate toDate) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Báo cáo");

        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = ((XSSFWorkbook) workbook).createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // Create header row
        Row headerRow = sheet.createRow(0);
        if ("marketing".equals(reportType)) {
            createMarketingExcelReport(sheet, headerRow, headerStyle);
        } else if ("expense".equals(reportType)) {
            createExpenseExcelReport(sheet, headerRow, headerStyle, fromDate, toDate);
        }

        // Autosize columns
        for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    public byte[] exportToPDF(String reportType, LocalDate fromDate, LocalDate toDate) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);

        document.open();
        document.addTitle("Báo cáo " + (reportType.equals("marketing") ? "Marketing" : "Chi phí"));

        // Add title
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 18, com.itextpdf.text.Font.BOLD);
        Paragraph title = new Paragraph("BÁO CÁO " + 
            (reportType.equals("marketing") ? "MARKETING" : "CHI PHÍ"), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n")); // Add spacing

        // Add date range if applicable
        if (fromDate != null && toDate != null) {
            com.itextpdf.text.Font dateFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            Paragraph dateRange = new Paragraph("Từ ngày: " + fromDate.format(formatter) + 
                " đến ngày: " + toDate.format(formatter), dateFont);
            dateRange.setAlignment(Element.ALIGN_CENTER);
            document.add(dateRange);
            document.add(new Paragraph("\n"));
        }

        // Create content based on report type
        if ("marketing".equals(reportType)) {
            createMarketingPDFReport(document);
        } else if ("expense".equals(reportType)) {
            createExpensePDFReport(document, fromDate, toDate);
        }

        document.close();
        return outputStream.toByteArray();
    }

    private void createMarketingExcelReport(Sheet sheet, Row headerRow, CellStyle headerStyle) {
        // Create headers
        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("Nguồn khách hàng");
        headerCell1.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("Số lượng");
        headerCell2.setCellStyle(headerStyle);

        // Add data
        Map<String, Long> marketingData = reportService.thongKeNguonKhachHang();
        int rowNum = 1;
        for (Map.Entry<String, Long> entry : marketingData.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(getMarketingSourceLabel(entry.getKey()));
            row.createCell(1).setCellValue(entry.getValue());
        }
    }

    private void createExpenseExcelReport(Sheet sheet, Row headerRow, CellStyle headerStyle, 
            LocalDate fromDate, LocalDate toDate) {
        // Create headers
        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("Thời gian");
        headerCell1.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("Chi phí (VNĐ)");
        headerCell2.setCellStyle(headerStyle);

        // Create currency style
        CellStyle currencyStyle = sheet.getWorkbook().createCellStyle();
        currencyStyle.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat("#,##0"));

        // Add data
        if (fromDate != null) {
            Map<String, BigDecimal> expenseData = reportService.thongKeChiPhi(
                fromDate.getYear(), 
                fromDate.getMonthValue()
            );

            int rowNum = 1;
            for (Map.Entry<String, BigDecimal> entry : expenseData.entrySet()) {
                if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                    Row row = sheet.createRow(rowNum++);
                    
                    // Format date
                    Cell dateCell = row.createCell(0);
                    dateCell.setCellValue(LocalDate.parse(entry.getKey()).format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    ));
                    
                    // Format currency
                    Cell amountCell = row.createCell(1);
                    amountCell.setCellValue(entry.getValue().doubleValue());
                    amountCell.setCellStyle(currencyStyle);
                }
            }
        }
    }

    private void createMarketingPDFReport(Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Add header
        com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12, com.itextpdf.text.Font.BOLD);
        table.addCell(new PdfPCell(new Phrase("Nguồn khách hàng", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Số lượng", headerFont)));

        // Add data
        com.itextpdf.text.Font contentFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12);
        Map<String, Long> marketingData = reportService.thongKeNguonKhachHang();
        for (Map.Entry<String, Long> entry : marketingData.entrySet()) {
            table.addCell(new PdfPCell(new Phrase(getMarketingSourceLabel(entry.getKey()), contentFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(entry.getValue()), contentFont)));
        }

        document.add(table);
    }

    private void createExpensePDFReport(Document document, LocalDate fromDate, LocalDate toDate) 
            throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Add header
        com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12, com.itextpdf.text.Font.BOLD);
        table.addCell(new PdfPCell(new Phrase("Thời gian", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Chi phí (VNĐ)", headerFont)));

        // Add data
        com.itextpdf.text.Font contentFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12);
        
        if (fromDate != null) {
            Map<String, BigDecimal> expenseData = reportService.thongKeChiPhi(
                fromDate.getYear(), 
                fromDate.getMonthValue()
            );

            NumberFormat formatter = NumberFormat.getInstance(VIETNAM_LOCALE);
            
            for (Map.Entry<String, BigDecimal> entry : expenseData.entrySet()) {
                if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                    // Format date
                    String dateStr = LocalDate.parse(entry.getKey())
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    
                    // Format amount
                    String amountStr = formatter.format(entry.getValue());
                    
                    table.addCell(new PdfPCell(new Phrase(dateStr, contentFont)));
                    table.addCell(new PdfPCell(new Phrase(amountStr, contentFont)));
                }
            }
        }

        document.add(table);
    }

    private String getMarketingSourceLabel(String key) {
        return switch (key) {
            case "friend" -> "Người quen giới thiệu";
            case "facebook" -> "Facebook";
            case "tiktok" -> "Tiktok";
            case "google" -> "Tìm kiếm trên Google";
            case "youtube" -> "Quảng cáo Youtube";
            case "website" -> "Website/Blog khác";
            default -> "Khác";
        };
    }
}
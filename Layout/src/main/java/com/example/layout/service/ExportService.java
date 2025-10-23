package com.example.layout.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.BaseFont;
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

    // Tìm font Arial trên hệ thống
    private String findArialFont() {
        String[] possiblePaths = {
            "c:/windows/fonts/arial.ttf",
            "c:/windows/fonts/Arial.ttf", 
            "/System/Library/Fonts/Arial.ttf",
            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
            "/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf"
        };
        
        for (String path : possiblePaths) {
            if (new java.io.File(path).exists()) {
                return path;
            }
        }
        return null;
    }

    // Tạo font Arial hỗ trợ tiếng Việt cho PDF
    private com.itextpdf.text.Font createVietnameseFont(float size, int style) {
        try {
            // Tìm và sử dụng font Arial trên hệ thống
            String arialPath = findArialFont();
            if (arialPath != null) {
                BaseFont bf = BaseFont.createFont(arialPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                return new com.itextpdf.text.Font(bf, size, style);
            }
        } catch (Exception e) {
            // Bỏ qua lỗi và thử fallback
        }

        try {
            // Fallback 1: Sử dụng Times Roman với IDENTITY_H encoding (hỗ trợ Unicode tốt)
            BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            return new com.itextpdf.text.Font(bf, size, style);
        } catch (Exception e) {
            try {
                // Fallback 2: Sử dụng Helvetica với IDENTITY_H encoding
                BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                return new com.itextpdf.text.Font(bf, size, style);
            } catch (Exception e2) {
                try {
                    // Fallback 3: Sử dụng CP1252 encoding cho tiếng Việt
                    BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, "Cp1252", BaseFont.NOT_EMBEDDED);
                    return new com.itextpdf.text.Font(bf, size, style);
                } catch (Exception e3) {
                    // Fallback cuối cùng
                    return new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, size, style);
                }
            }
        }
    }

    // Xử lý text tiếng Việt để đảm bảo hiển thị đúng trong PDF
    private String processVietnameseText(String text) {
        if (text == null) return "";
        
        try {
            // Đảm bảo text được encode đúng UTF-8
            byte[] utf8Bytes = text.getBytes("UTF-8");
            return new String(utf8Bytes, "UTF-8");
        } catch (Exception e) {
            return text; // Trả về text gốc nếu có lỗi
        }
    }

    public byte[] exportToExcel(String reportType, LocalDate fromDate, LocalDate toDate) throws IOException {
        try {
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
        if ("marketing".equals(reportType) || "customers".equals(reportType)) {
            createMarketingExcelReport(sheet, headerRow, headerStyle);
        } else if ("bookings".equals(reportType)) {
            createBookingStatusExcelReport(sheet, headerRow, headerStyle);
        } else if ("expense".equals(reportType)) {
            createExpenseExcelReport(sheet, headerRow, headerStyle, fromDate, toDate, "Chi phí");
        } else if ("revenue".equals(reportType)) {
            createExpenseExcelReport(sheet, headerRow, headerStyle, fromDate, toDate, "Doanh thu");
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
        } catch (Exception e) {
            // If POI fails, return a simple CSV-like Excel file
            return createSimpleExcelFallback(reportType, fromDate, toDate);
        }
    }

    private byte[] createSimpleExcelFallback(String reportType, LocalDate fromDate, LocalDate toDate) throws IOException {
        StringBuilder csvContent = new StringBuilder();
        
        // Xử lý title báo cáo
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
                Map<String, BigDecimal> revenueData = reportService.thongKeChiPhi(fromDate.getYear(), fromDate.getMonthValue());
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

    public byte[] exportToPDF(String reportType, LocalDate fromDate, LocalDate toDate) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);

        document.open();
        
        // Xử lý title
        String titleText = "";
        if ("marketing".equals(reportType) || "customers".equals(reportType)) {
            titleText = "NGUỒN KHÁCH HÀNG";
        } else if ("bookings".equals(reportType)) {
            titleText = "TRẠNG THÁI ĐẶT TOUR";
        } else if ("expense".equals(reportType)) {
            titleText = "CHI PHÍ";
        } else if ("revenue".equals(reportType)) {
            titleText = "DOANH THU";
        }
        document.addTitle("Báo cáo " + titleText);

        // Add title
        com.itextpdf.text.Font titleFont = createVietnameseFont(18, com.itextpdf.text.Font.BOLD);
        Paragraph title = new Paragraph(processVietnameseText("BÁO CÁO " + titleText), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n")); // Add spacing

        // Add date range if applicable
        if (fromDate != null && toDate != null) {
            com.itextpdf.text.Font dateFont = createVietnameseFont(12, com.itextpdf.text.Font.NORMAL);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dateRangeText = "Từ ngày: " + fromDate.format(formatter) + 
                " đến ngày: " + toDate.format(formatter);
            Paragraph dateRange = new Paragraph(processVietnameseText(dateRangeText), dateFont);
            dateRange.setAlignment(Element.ALIGN_CENTER);
            document.add(dateRange);
            document.add(new Paragraph("\n"));
        }

        // Create content based on report type
        if ("marketing".equals(reportType) || "customers".equals(reportType)) {
            createMarketingPDFReport(document);
        } else if ("bookings".equals(reportType)) {
            createBookingStatusPDFReport(document);
        } else if ("expense".equals(reportType)) {
            createExpensePDFReport(document, fromDate, toDate, "Chi phí");
        } else if ("revenue".equals(reportType)) {
            createExpensePDFReport(document, fromDate, toDate, "Doanh thu");
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
            LocalDate fromDate, LocalDate toDate, String type) {
        // Create headers
        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("Thời gian");
        headerCell1.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue(type + " (VNĐ)");
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
        com.itextpdf.text.Font headerFont = createVietnameseFont(12, com.itextpdf.text.Font.BOLD);
        table.addCell(new PdfPCell(new Phrase(processVietnameseText("Nguồn khách hàng"), headerFont)));
        table.addCell(new PdfPCell(new Phrase(processVietnameseText("Số lượng"), headerFont)));

        // Add data
        com.itextpdf.text.Font contentFont = createVietnameseFont(12, com.itextpdf.text.Font.NORMAL);
        Map<String, Long> marketingData = reportService.thongKeNguonKhachHang();
        for (Map.Entry<String, Long> entry : marketingData.entrySet()) {
            table.addCell(new PdfPCell(new Phrase(processVietnameseText(getMarketingSourceLabel(entry.getKey())), contentFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(entry.getValue()), contentFont)));
        }

        document.add(table);
    }

    private void createExpensePDFReport(Document document, LocalDate fromDate, LocalDate toDate, String type) 
            throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Add header
        com.itextpdf.text.Font headerFont = createVietnameseFont(12, com.itextpdf.text.Font.BOLD);
        table.addCell(new PdfPCell(new Phrase(processVietnameseText("Thời gian"), headerFont)));
        table.addCell(new PdfPCell(new Phrase(processVietnameseText(type + " (VNĐ)"), headerFont)));

        // Add data
        com.itextpdf.text.Font contentFont = createVietnameseFont(12, com.itextpdf.text.Font.NORMAL);
        
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

    private void createBookingStatusExcelReport(Sheet sheet, Row headerRow, CellStyle headerStyle) {
        // Create headers
        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("Trạng thái đặt tour");
        headerCell1.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("Số lượng");
        headerCell2.setCellStyle(headerStyle);

        // Add data
        Map<String, Long> bookingData = reportService.thongKeTrangThaiDatCho();
        int rowNum = 1;
        for (Map.Entry<String, Long> entry : bookingData.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }
    }

    private void createBookingStatusPDFReport(Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Add header
        com.itextpdf.text.Font headerFont = createVietnameseFont(12, com.itextpdf.text.Font.BOLD);
        table.addCell(new PdfPCell(new Phrase(processVietnameseText("Trạng thái đặt tour"), headerFont)));
        table.addCell(new PdfPCell(new Phrase(processVietnameseText("Số lượng"), headerFont)));

        // Add data
        com.itextpdf.text.Font contentFont = createVietnameseFont(12, com.itextpdf.text.Font.NORMAL);
        Map<String, Long> bookingData = reportService.thongKeTrangThaiDatCho();
        for (Map.Entry<String, Long> entry : bookingData.entrySet()) {
            table.addCell(new PdfPCell(new Phrase(processVietnameseText(entry.getKey()), contentFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(entry.getValue()), contentFont)));
        }

        document.add(table);
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

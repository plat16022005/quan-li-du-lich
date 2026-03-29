package com.example.layout.service.impl;

import com.example.layout.service.IPdfExportService;
import com.example.layout.service.IReportService;
import com.example.layout.utils.MarketingSourceUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@Service
public class PdfExportServiceImpl implements IPdfExportService {

    private final IReportService reportService;
    private static final Locale VIETNAM_LOCALE = Locale.forLanguageTag("vi-VN");

    public PdfExportServiceImpl(IReportService reportService) {
        this.reportService = reportService;
    }

    private String findArialFont() {
        String[] possiblePaths = {
            "c:/windows/fonts/arial.ttf",
            "c:/windows/fonts/Arial.ttf", 
            "/System/Library/Fonts/Arial.ttf",
            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
            "/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf"
        };
        for (String path : possiblePaths) {
            if (new java.io.File(path).exists()) return path;
        }
        return null;
    }

    private com.itextpdf.text.Font createVietnameseFont(float size, int style) {
        try {
            String arialPath = findArialFont();
            if (arialPath != null) {
                BaseFont bf = BaseFont.createFont(arialPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                return new com.itextpdf.text.Font(bf, size, style);
            }
        } catch (Exception e) {}

        try {
            BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            return new com.itextpdf.text.Font(bf, size, style);
        } catch (Exception e) {
            return new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, size, style);
        }
    }

    private String processVietnameseText(String text) {
        if (text == null) return "";
        try {
            return new String(text.getBytes("UTF-8"), "UTF-8");
        } catch (Exception e) {
            return text; 
        }
    }

    public byte[] exportToPDF(String reportType, LocalDate fromDate, LocalDate toDate) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);

        document.open();
        
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

        com.itextpdf.text.Font titleFont = createVietnameseFont(18, com.itextpdf.text.Font.BOLD);
        Paragraph title = new Paragraph(processVietnameseText("BÁO CÁO " + titleText), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        if (fromDate != null && toDate != null) {
            com.itextpdf.text.Font dateFont = createVietnameseFont(12, com.itextpdf.text.Font.NORMAL);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dateRangeText = "Từ ngày: " + fromDate.format(formatter) + " đến ngày: " + toDate.format(formatter);
            Paragraph dateRange = new Paragraph(processVietnameseText(dateRangeText), dateFont);
            dateRange.setAlignment(Element.ALIGN_CENTER);
            document.add(dateRange);
            document.add(new Paragraph("\n"));
        }

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

    private void createMarketingPDFReport(Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        com.itextpdf.text.Font headerFont = createVietnameseFont(12, com.itextpdf.text.Font.BOLD);
        table.addCell(new PdfPCell(new Phrase(processVietnameseText("Nguồn khách hàng"), headerFont)));
        table.addCell(new PdfPCell(new Phrase(processVietnameseText("Số lượng"), headerFont)));

        com.itextpdf.text.Font contentFont = createVietnameseFont(12, com.itextpdf.text.Font.NORMAL);
        Map<String, Long> marketingData = reportService.thongKeNguonKhachHang();
        for (Map.Entry<String, Long> entry : marketingData.entrySet()) {
            table.addCell(new PdfPCell(new Phrase(processVietnameseText(MarketingSourceUtils.getLabel(entry.getKey())), contentFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(entry.getValue()), contentFont)));
        }

        document.add(table);
    }

    private void createExpensePDFReport(Document document, LocalDate fromDate, LocalDate toDate, String type) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        com.itextpdf.text.Font headerFont = createVietnameseFont(12, com.itextpdf.text.Font.BOLD);
        table.addCell(new PdfPCell(new Phrase(processVietnameseText("Thời gian"), headerFont)));
        table.addCell(new PdfPCell(new Phrase(processVietnameseText(type + " (VNĐ)"), headerFont)));

        com.itextpdf.text.Font contentFont = createVietnameseFont(12, com.itextpdf.text.Font.NORMAL);
        if (fromDate != null) {
            Map<String, BigDecimal> expenseData = reportService.thongKeChiPhi(fromDate.getYear(), fromDate.getMonthValue());
            NumberFormat formatter = NumberFormat.getInstance(VIETNAM_LOCALE);
            for (Map.Entry<String, BigDecimal> entry : expenseData.entrySet()) {
                if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                    String dateStr = LocalDate.parse(entry.getKey()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    String amountStr = formatter.format(entry.getValue());
                    table.addCell(new PdfPCell(new Phrase(dateStr, contentFont)));
                    table.addCell(new PdfPCell(new Phrase(amountStr, contentFont)));
                }
            }
        }
        document.add(table);
    }

    private void createBookingStatusPDFReport(Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        com.itextpdf.text.Font headerFont = createVietnameseFont(12, com.itextpdf.text.Font.BOLD);
        table.addCell(new PdfPCell(new Phrase(processVietnameseText("Trạng thái đặt tour"), headerFont)));
        table.addCell(new PdfPCell(new Phrase(processVietnameseText("Số lượng"), headerFont)));

        com.itextpdf.text.Font contentFont = createVietnameseFont(12, com.itextpdf.text.Font.NORMAL);
        Map<String, Long> bookingData = reportService.thongKeTrangThaiDatCho();
        for (Map.Entry<String, Long> entry : bookingData.entrySet()) {
            table.addCell(new PdfPCell(new Phrase(processVietnameseText(entry.getKey()), contentFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(entry.getValue()), contentFont)));
        }

        document.add(table);
    }

}

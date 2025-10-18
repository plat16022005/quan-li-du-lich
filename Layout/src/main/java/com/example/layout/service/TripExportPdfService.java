package com.example.layout.service;

import com.example.layout.dto.HanhKhachDTO;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.LichTrinh;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TripExportPdfService {

    private static Font TITLE_FONT;
    private static Font HEADER_FONT;
    private static Font NORMAL_FONT;
    private static Font BOLD_FONT;

    static {
        try {
            // Sử dụng font Arial Unicode MS hỗ trợ tiếng Việt
            BaseFont baseFont = BaseFont.createFont("c:/windows/fonts/ARIAL.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            
            TITLE_FONT = new Font(baseFont, 18, Font.BOLD);
            HEADER_FONT = new Font(baseFont, 14, Font.BOLD);
            NORMAL_FONT = new Font(baseFont, 12, Font.NORMAL);
            BOLD_FONT = new Font(baseFont, 12, Font.BOLD);
        } catch (Exception e) {
            System.err.println("Không thể load font Arial: " + e.getMessage());
            // Fallback về font mặc định
            TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 12);
            BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        }
    }

    public byte[] exportTripDetailsToPdf(ChuyenDuLich chuyen,
                                         int soLuongHanhKhach,
                                         List<HanhKhachDTO> danhSachHanhKhach,
                                         List<LichTrinh> lichTrinh) throws DocumentException, IOException {

        Document document = new Document(PageSize.A4, 36, 36, 54, 54);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // ========== TIÊU ĐỀ ==========
        Paragraph title = new Paragraph("CHI TIẾT CHUYẾN ĐI", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // ========== THÔNG TIN CHUYẾN ĐI ==========
        document.add(new Paragraph("I. THÔNG TIN CHUYẾN ĐI", HEADER_FONT));
        document.add(Chunk.NEWLINE);

        if (chuyen.getTour() != null) {
            addInfoLine(document, "Tên Tour: ", chuyen.getTour().getTenTour());
            
            long soNgay = chuyen.getTour().getSoNgay();
            if (soNgay <= 0 && chuyen.getNgayBatDau() != null && chuyen.getNgayKetThuc() != null) {
                soNgay = ChronoUnit.DAYS.between(chuyen.getNgayBatDau(), chuyen.getNgayKetThuc()) + 1;
            }
            
            addInfoLine(document, "Giá: ", formatCurrency(chuyen.getTour().getGiaCoBan()) + " VNĐ");
        } else {
            addInfoLine(document, "Loại: ", "Chuyến đi riêng lẻ");
        }

        addInfoLine(document, "Mã chuyến: ", String.valueOf(chuyen.getMaChuyen()));
        addInfoLine(document, "Ngày khởi hành: ", chuyen.getNgayBatDau().format(formatter));
        addInfoLine(document, "Ngày kết thúc: ", chuyen.getNgayKetThuc().format(formatter));
        addInfoLine(document, "Trạng thái: ", chuyen.getTrangThai());
        addInfoLine(document, "Số lượng: ", soLuongHanhKhach + " / " + chuyen.getSoLuongToiDa() + " người");
        document.add(Chunk.NEWLINE);

     // ========== DANH SÁCH HÀNH KHÁCH ==========
        document.add(new Paragraph("II. DANH SÁCH HÀNH KHÁCH", HEADER_FONT));
        document.add(Chunk.NEWLINE);

        if (!danhSachHanhKhach.isEmpty()) {
            // ✅ THAY ĐỔI: 4 cột -> 5 cột
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(10);
            
            // ✅ Thiết lập độ rộng cột (tổng = 100)
            table.setWidths(new float[]{25, 20, 30, 15, 10});

            // ✅ Header - THÊM CỘT "SỐ LƯỢNG"
            addTableHeader(table, "Họ tên", "Số điện thoại", "Email", "Giới tính", "Số lượng");

            // ✅ Data - THÊM CELL SỐ LƯỢNG
            for (HanhKhachDTO khach : danhSachHanhKhach) {
                table.addCell(new Phrase(khach.getHoTen(), NORMAL_FONT));
                table.addCell(new Phrase(khach.getSoDienThoai(), NORMAL_FONT));
                table.addCell(new Phrase(khach.getEmail(), NORMAL_FONT));
                table.addCell(new Phrase(khach.getGioiTinh(), NORMAL_FONT));
                
                // ✅ THÊM CỘT SỐ LƯỢNG
                PdfPCell cellSoLuong = new PdfPCell(new Phrase(
                    String.valueOf(khach.getSoLuong() != null ? khach.getSoLuong() : 1), 
                    NORMAL_FONT
                ));
                cellSoLuong.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellSoLuong);
            }

            document.add(table);
        } else {
            document.add(new Paragraph("Chưa có hành khách nào đăng ký.", NORMAL_FONT));
        }

        document.add(Chunk.NEWLINE);

        // ========== LỊCH TRÌNH CHI TIẾT ==========
        document.add(new Paragraph("III. LỊCH TRÌNH CHI TIẾT", HEADER_FONT));
        document.add(Chunk.NEWLINE);

        if (lichTrinh != null && !lichTrinh.isEmpty()) {
            for (LichTrinh lt : lichTrinh) {
                Paragraph dayPara = new Paragraph("Ngày " + lt.getThuTuNgay() + ": " + lt.getHoatDong(), BOLD_FONT);
                dayPara.setSpacingBefore(5);
                document.add(dayPara);

                if (lt.getDiaDiem() != null) {
                    document.add(new Paragraph("  • Địa điểm: " + lt.getDiaDiem().getTenDiaDiem(), NORMAL_FONT));
                }
                if (lt.getKhachSan() != null) {
                    document.add(new Paragraph("  • Khách sạn: " + lt.getKhachSan().getTenKhachSan(), NORMAL_FONT));
                }
                if (lt.getPhuongTien() != null) {
                    document.add(new Paragraph("  • Phương tiện: " + lt.getPhuongTien().getLoaiPhuongTien() + 
                                              " (" + lt.getPhuongTien().getBienSo() + ")", NORMAL_FONT));
                }
                document.add(Chunk.NEWLINE);
            }
        } else {
            document.add(new Paragraph("Chưa có lịch trình chi tiết.", NORMAL_FONT));
        }

        document.close();
        return out.toByteArray();
    }

    private void addInfoLine(Document document, String label, String value) throws DocumentException {
        Paragraph para = new Paragraph();
        para.add(new Chunk(label, BOLD_FONT));
        para.add(new Chunk(value != null ? value : "N/A", NORMAL_FONT));
        document.add(para);
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private String formatCurrency(java.math.BigDecimal amount) {
        if (amount == null) return "0";
        return String.format("%,d", amount.longValue());
    }
}
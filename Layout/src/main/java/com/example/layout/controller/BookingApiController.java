package com.example.layout.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.layout.dto.BookingResponseDTO;
import com.example.layout.entity.ChiTietDatCho;
import com.example.layout.entity.DatCho;
import com.example.layout.entity.ThanhToan;
import com.example.layout.repository.DatChoRepository;
import com.example.layout.repository.ThanhToanRepository;

@RestController
@RequestMapping("/api/booking")
public class BookingApiController {

    private final DatChoRepository datChoRepository;
    private final ThanhToanRepository thanhToanRepo;

    public BookingApiController(DatChoRepository datChoRepository, ThanhToanRepository thanhToanRepo) {
        this.datChoRepository = datChoRepository;
        this.thanhToanRepo = thanhToanRepo;
    }

    @GetMapping("/{id}")
    public BookingResponseDTO getBooking(@PathVariable("id") Integer id) {
        DatCho datCho = datChoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt chỗ ID: " + id));

        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setMaDatCho(datCho.getMaDatCho());
        dto.setTenTour(datCho.getChuyenDuLich().getTour().getTenTour());
        dto.setNgayKhoiHanh(datCho.getChuyenDuLich().getNgayBatDau());
        dto.setTongTien(
            datCho.getChiTietDatChos().stream()
                .map(ChiTietDatCho::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        return dto;
    }
    
    @PutMapping("/{id}/pay")
    public ResponseEntity<String> payBooking(@PathVariable("id") Integer id, @RequestBody(required = false) PaymentRequest paymentRequest) {
        DatCho datCho = datChoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt chỗ ID: " + id));

        datCho.setTrangThai("Đã thanh toán");
        datChoRepository.save(datCho);
        ThanhToan thanhToan = new ThanhToan();
        thanhToan.setDatCho(datCho);
        thanhToan.setNgayThanhToan(LocalDate.now());
        thanhToan.setHinhThuc(paymentRequest != null ? paymentRequest.phuongThuc : "bank");
        thanhToan.setSoTien(BigDecimal.valueOf(datCho.getChuyenDuLich().getTour().getGiaCoBan().doubleValue()));
        thanhToanRepo.save(thanhToan);
        return ResponseEntity.ok("Cập nhật trạng thái thành công");
    }
    public static class PaymentRequest {
        public String phuongThuc;
    }
}


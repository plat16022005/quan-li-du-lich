package com.example.layout.service;

import com.example.layout.dto.BookingDTO;
import com.example.layout.entity.DatCho;
import com.example.layout.entity.KhachHang;
import com.example.layout.repository.DatChoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatChoService {

    @Autowired
    private DatChoRepository datChoRepository;

    public List<BookingDTO> getBookingsByTourId(Integer tourId) {
        List<DatCho> bookings = datChoRepository.findByTourMaTour(tourId);
        
        return bookings.stream().map(booking -> {
            KhachHang khachHang = booking.getKhachHang();
            return new BookingDTO(
                booking.getMaDatCho(),
                khachHang.getTaiKhoan().getHoTen(),
                khachHang.getTaiKhoan().getSoDienThoai(),
                booking.getNgayDat(),
                booking.getTrangThai()
            );
        }).collect(Collectors.toList());
    }

    public void approveBooking(Integer bookingId) {
        DatCho booking = datChoRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt chỗ"));
        booking.setTrangThai("Đã duyệt");
        datChoRepository.save(booking);
    }

    public void rejectBooking(Integer bookingId) {
        DatCho booking = datChoRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt chỗ"));
        booking.setTrangThai("Đã hủy");
        datChoRepository.save(booking);
    }
}
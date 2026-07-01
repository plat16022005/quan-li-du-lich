package com.example.layout.service;

import com.example.layout.dto.BookingApiDTO;
import com.example.layout.dto.BookingDTO;
import com.example.layout.dto.DatChoDTO;
import com.example.layout.entity.DatCho;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IDatChoService {
    Optional<DatCho> findById(Integer id);
    Page<DatCho> findall(Pageable pageable);
    Page<DatCho> searchAndFilter(String keyword, String status, Pageable pageable);
    Page<BookingApiDTO> searchAndFilterDto(String keyword, String status, Pageable pageable);
    List<DatCho> findByKhachHangId(Integer maKhachHang);
    DatCho createBooking(DatChoDTO datChoDTO);
    BookingApiDTO getBookingApiDtoById(Integer bookingId);
    DatCho cancelBooking(Integer maDatCho);
    DatCho confirmBooking(Integer maDatCho);
    List<BookingDTO> getBookingsByTourId(Integer tourId);
    Long getSoldTicketCount(Integer maChuyen);
    List<com.example.layout.dto.TopCustomerDTO> findTopCustomers();
    List<DatCho> findByChuyenDuLich_Tour_MaTour(Integer maTour);
}

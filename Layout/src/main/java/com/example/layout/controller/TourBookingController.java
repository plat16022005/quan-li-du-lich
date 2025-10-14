package com.example.layout.controller;

import com.example.layout.dto.BookingDTO;
import com.example.layout.service.DatChoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager/tour")
public class TourBookingController {

    @Autowired
    private DatChoService datChoService;

    @GetMapping("/{tourId}/bookings")
    public ResponseEntity<List<BookingDTO>> getBookings(@PathVariable Integer tourId) {
        return ResponseEntity.ok(datChoService.getBookingsByTourId(tourId));
    }

    @PutMapping("/bookings/{bookingId}/approve")
    public ResponseEntity<?> approveBooking(@PathVariable Integer bookingId) {
        datChoService.confirmBooking(bookingId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/bookings/{bookingId}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable Integer bookingId) {
        datChoService.cancelBooking(bookingId);
        return ResponseEntity.ok().build();
    }
}
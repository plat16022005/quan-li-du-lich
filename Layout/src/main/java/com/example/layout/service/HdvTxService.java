package com.example.layout.service;

import com.example.layout.controller.hdvtx.HdvTxDashboardController.CurrentUserDTO;
import com.example.layout.entity.ChuyenDuLich;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface HdvTxService {
    List<ChuyenDuLich> getAvailableTrips(CurrentUserDTO currentUser);
    List<ChuyenDuLich> getAssignedTrips(CurrentUserDTO currentUser);
    void cancelTripAssignment(int tripId, CurrentUserDTO currentUser);
    void assignTripToEmployee(int tripId, CurrentUserDTO currentUser);
    Set<Integer> findConflictingTripIds(List<ChuyenDuLich> trips);
    
    List<ChuyenDuLich> searchAssignedTrips(
            CurrentUserDTO currentUser, 
            String trangThai, 
            String tenTour, 
            LocalDate tuNgay, 
            LocalDate denNgay
        );
        
        List<ChuyenDuLich> searchAvailableTrips(
            CurrentUserDTO currentUser, 
            String tenTour, 
            LocalDate tuNgay, 
            LocalDate denNgay
        );
}
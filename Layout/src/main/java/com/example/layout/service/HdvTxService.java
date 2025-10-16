package com.example.layout.service;

import com.example.layout.controller.hdvtx.HdvTxDashboardController.CurrentUserDTO;
import com.example.layout.entity.ChuyenDuLich;
import java.util.List;

public interface HdvTxService {
    List<ChuyenDuLich> getAvailableTrips(CurrentUserDTO currentUser);
    List<ChuyenDuLich> getAssignedTrips(CurrentUserDTO currentUser);
    void cancelTripAssignment(int tripId, CurrentUserDTO currentUser);
    void assignTripToEmployee(int tripId, CurrentUserDTO currentUser);
}
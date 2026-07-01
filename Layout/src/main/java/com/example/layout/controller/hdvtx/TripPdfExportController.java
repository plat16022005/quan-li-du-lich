package com.example.layout.controller.hdvtx;

import com.example.layout.dto.HanhKhachDTO;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.LichTrinh;
import com.example.layout.service.IChuyenDuLichService;
import com.example.layout.service.ILichTrinhService;
import com.example.layout.service.ITripExportPdfService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/hdvtx/trip-details")
public class TripPdfExportController {

    private final IChuyenDuLichService chuyenDuLichService;
    private final ILichTrinhService lichTrinhService;
    private final ITripExportPdfService tripExportPdfService;

    public TripPdfExportController(IChuyenDuLichService chuyenDuLichService,
                                   ILichTrinhService lichTrinhService,
                                   ITripExportPdfService tripExportPdfService) {
        this.chuyenDuLichService = chuyenDuLichService;
        this.lichTrinhService = lichTrinhService;
        this.tripExportPdfService = tripExportPdfService;
    }


    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadTripPdf(@PathVariable("id") Integer tripId) {
        try {

            ChuyenDuLich chuyen = chuyenDuLichService.getChuyenById(tripId);
            if (chuyen == null) {
                throw new RuntimeException("Không tìm thấy chuyến đi");
            }

            int soLuongHanhKhach = chuyenDuLichService.getTotalParticipants(tripId);
            
            List<HanhKhachDTO> danhSachHanhKhach = chuyenDuLichService.getHanhKhachByMaChuyen(tripId);


            List<LichTrinh> lichTrinh = java.util.Collections.emptyList();
            if (chuyen.getTour() != null && chuyen.getTour().getMaTour() != null) {
                lichTrinh = lichTrinhService.getLichTrinhByTour(chuyen.getTour().getMaTour());
            }

            byte[] pdfBytes = tripExportPdfService.exportTripDetailsToPdf(
                    chuyen,
                    soLuongHanhKhach,
                    danhSachHanhKhach,
                    lichTrinh
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "chuyen_" + tripId + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
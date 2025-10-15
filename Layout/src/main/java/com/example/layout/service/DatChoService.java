package com.example.layout.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.layout.entity.ChiTietDatCho;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.DatCho;
import com.example.layout.entity.KhachHang;
import com.example.layout.repository.ChiTietDatChoRepository;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.repository.DatChoRepository;
import com.example.layout.repository.KhachHangRepository;
import com.example.layout.dto.BookingDTO;
import com.example.layout.dto.DatChoDTO;
import jakarta.transaction.Transactional;

@Service
public class DatChoService {
    private final DatChoRepository datChoRepository;
    private final ChiTietDatChoRepository chiTietDatChoRepository;
    private final ChuyenDuLichRepository chuyenDuLichRepository;
    private final KhachHangRepository khachHangRepository;

    public DatChoService(DatChoRepository datChoRepository,
                        ChiTietDatChoRepository chiTietDatChoRepository,
                        ChuyenDuLichRepository chuyenDuLichRepository,
                        KhachHangRepository khachHangRepository) {
        this.datChoRepository = datChoRepository;
        this.chiTietDatChoRepository = chiTietDatChoRepository;
        this.chuyenDuLichRepository = chuyenDuLichRepository;
        this.khachHangRepository = khachHangRepository;
    }

    public Optional<DatCho> findById(Integer id) {
        return datChoRepository.findById(id);
    }
    
    public Page<DatCho> findall(Pageable pageable){
    	return datChoRepository.findAll(pageable);
    }
    
    public Page<DatCho> searchAndFilter(String keyword, String status, Pageable pageable) {
        return datChoRepository.searchAndFilter(keyword, status, pageable);
    }

    public List<DatCho> findByKhachHangId(Integer maKhachHang) {
        return datChoRepository.findByKhachHang_MaKhachHang(maKhachHang);
    }


    @Transactional
    public DatCho createBooking(DatChoDTO datChoDTO) {
        ChuyenDuLich chuyen = chuyenDuLichRepository.findById(datChoDTO.getMaChuyen())
                .orElseThrow(() -> new RuntimeException("Chuyến đi không tồn tại"));
        
        KhachHang khach = khachHangRepository.findById(datChoDTO.getMaKhachHang())
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        int soNguoiDaDat = chuyenDuLichRepository.getTotalParticipants(chuyen.getMaChuyen());
        if (soNguoiDaDat + datChoDTO.getSoLuong() > chuyen.getSoLuongToiDa()) {
            throw new RuntimeException("Chuyến đi không còn đủ chỗ");
        }

        DatCho newDatCho = new DatCho();
        newDatCho.setChuyenDuLich(chuyen);
        newDatCho.setKhachHang(khach);
        newDatCho.setTrangThai("Chờ xác nhận");
        DatCho savedDatCho = datChoRepository.save(newDatCho);

        ChiTietDatCho chiTiet = new ChiTietDatCho();
        chiTiet.setDatCho(savedDatCho);
        chiTiet.setSoLuong(datChoDTO.getSoLuong());
        chiTiet.setDonGia(chuyen.getTour().getGiaCoBan());
        
        BigDecimal thanhTien = chiTiet.getDonGia().multiply(new BigDecimal(chiTiet.getSoLuong()));
        chiTiet.setThanhTien(thanhTien);
        chiTiet.setLoaiVe("Người lớn");
        
        chiTietDatChoRepository.save(chiTiet);
        
        return savedDatCho;
    }

    public DatCho cancelBooking(Integer maDatCho) {
        DatCho datCho = datChoRepository.findById(maDatCho)
                .orElseThrow(() -> new RuntimeException("Đơn đặt chỗ không tồn tại"));
        
        if ("Chờ xác nhận".equals(datCho.getTrangThai()) || "Đã xác nhận".equals(datCho.getTrangThai())) {
            datCho.setTrangThai("Đã hủy");
            return datChoRepository.save(datCho);
        } else {
            throw new RuntimeException("Không thể hủy đơn đặt chỗ ở trạng thái " + datCho.getTrangThai());
        }
    }
    
    public DatCho confirmBooking(Integer maDatCho) {
        DatCho datCho = datChoRepository.findById(maDatCho)
                .orElseThrow(() -> new RuntimeException("Đơn đặt chỗ không tồn tại"));

        if ("Chờ xác nhận".equals(datCho.getTrangThai())) {
            datCho.setTrangThai("Đã xác nhận");
            return datChoRepository.save(datCho);
        } else {
            throw new RuntimeException("Chỉ có thể xác nhận các đơn ở trạng thái 'Chờ xác nhận'.");
        }
    }
    
    public List<BookingDTO> getBookingsByTourId(Integer tourId) {
        List<DatCho> bookings = datChoRepository.findByChuyenDuLich_Tour_MaTour(tourId);
        
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
    
}

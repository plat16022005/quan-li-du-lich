package com.example.layout.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.layout.entity.DatCho;
import com.example.layout.entity.ThanhToan;
import com.example.layout.repository.ChiTietDatChoRepository;
import com.example.layout.repository.DatChoRepository;
import com.example.layout.repository.ThanhToanRepository;
import com.example.layout.dto.ThanhToanDTO;
import jakarta.transaction.Transactional;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class ThanhToanService {
    private final ThanhToanRepository thanhToanRepository;
    private final DatChoRepository datChoRepository;
    private final ChiTietDatChoRepository chiTietDatChoRepository;

    public ThanhToanService(ThanhToanRepository thanhToanRepository,
                            DatChoRepository datChoRepository,
                            ChiTietDatChoRepository chiTietDatChoRepository) {
        this.thanhToanRepository = thanhToanRepository;
        this.datChoRepository = datChoRepository;
        this.chiTietDatChoRepository = chiTietDatChoRepository;
    }

    /**
     * Lấy lịch sử thanh toán của một đơn đặt chỗ.
     */
    public List<ThanhToan> findByDatChoId(Integer maDatCho) {
        return thanhToanRepository.findByDatCho_MaDatCho(maDatCho);
    }

    /**
     * Trả về danh sách ThanhToanDTO cho một đơn đặt chỗ.
     * Sử dụng DTO để tránh vấn đề LazyInitializationException khi Jackson serialize.
     */
    @Transactional
    public List<ThanhToanDTO> findDtoByDatChoId(Integer maDatCho) {
        List<ThanhToan> payments = thanhToanRepository.findByDatCho_MaDatCho(maDatCho);
        if (payments == null || payments.isEmpty()) {
            return new ArrayList<>();
        }
        return payments.stream().map(t -> {
            ThanhToanDTO dto = new ThanhToanDTO();
            // Only include the minimal fields needed by the client
            if (t.getDatCho() != null) {
                dto.setMaDatCho(t.getDatCho().getMaDatCho());
            } else {
                dto.setMaDatCho(maDatCho);
            }
            dto.setSoTien(t.getSoTien());
            dto.setNgayThanhToan(t.getNgayThanhToan());
            dto.setHinhThuc(t.getHinhThuc());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Nghiệp vụ chính: Ghi nhận một giao dịch thanh toán mới.
     */
    @Transactional
    public ThanhToan recordPayment(ThanhToanDTO thanhToanDTO) {
        DatCho datCho = datChoRepository.findById(thanhToanDTO.getMaDatCho())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt chỗ."));

        ThanhToan newThanhToan = new ThanhToan();
        newThanhToan.setDatCho(datCho);
        newThanhToan.setSoTien(thanhToanDTO.getSoTien());
        newThanhToan.setHinhThuc(thanhToanDTO.getHinhThuc());

        ThanhToan savedThanhToan = thanhToanRepository.save(newThanhToan);

        updateBookingStatusAfterPayment(datCho.getMaDatCho());

        return savedThanhToan;
    }

    /**
     * Phương thức nội bộ để kiểm tra và cập nhật trạng thái đơn đặt chỗ.
     */
    private void updateBookingStatusAfterPayment(Integer maDatCho) {
        DatCho datCho = datChoRepository.findById(maDatCho)
                .orElseThrow(() -> new RuntimeException("Đơn đặt chỗ không tồn tại khi cập nhật trạng thái thanh toán"));

        BigDecimal totalAmountDue = chiTietDatChoRepository.findTotalAmountByDatCho_MaDatCho(maDatCho);
        if (totalAmountDue == null) {
            totalAmountDue = java.math.BigDecimal.ZERO;
        }
        BigDecimal totalAmountPaid = thanhToanRepository.findTotalPaidByDatChoId(maDatCho);
        if (totalAmountPaid == null) {
            totalAmountPaid = java.math.BigDecimal.ZERO;
        }

        try {
            if (totalAmountPaid.compareTo(totalAmountDue) >= 0) {
                datCho.setTrangThai("Đã thanh toán");
                datChoRepository.save(datCho);
            }
        } catch (Exception e) {
            // Log and swallow to avoid failing the payment recording when status update has transient issues
            System.err.println("Lỗi khi cập nhật trạng thái đơn sau thanh toán: " + e.getMessage());
        }
    }
        
}

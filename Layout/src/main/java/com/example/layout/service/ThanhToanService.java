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
        DatCho datCho = datChoRepository.findById(maDatCho).get();

        BigDecimal totalAmountDue = chiTietDatChoRepository.findTotalAmountByDatCho_MaDatCho(maDatCho);
        BigDecimal totalAmountPaid = thanhToanRepository.findTotalPaidByDatChoId(maDatCho);

        if (totalAmountPaid != null && totalAmountPaid.compareTo(totalAmountDue) >= 0) {
            datCho.setTrangThai("Đã thanh toán");
            datChoRepository.save(datCho);
        }
    }
        
}
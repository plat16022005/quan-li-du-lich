package com.example.layout.service;

import com.example.layout.entity.DiaDiem;
import com.example.layout.entity.KhachSan;
import com.example.layout.entity.LichTrinh;
import com.example.layout.entity.PhuongTien;
import com.example.layout.entity.Tour;

import java.util.List;
import java.util.Optional;

public interface ILichTrinhService {
    List<LichTrinh> getLichTrinhByTourOrderByNgay(Integer maTour);
    List<LichTrinh> getLichTrinhByTour(Integer maTour);
    Optional<LichTrinh> findById(Integer maLichTrinh);
    LichTrinh saveLichTrinh(LichTrinh lt);
    LichTrinh update(Integer maLichTrinh, LichTrinh updatedData);
    void deleteLichTrinh(Integer maLichTrinh);
    List<LichTrinh> saveAll(List<LichTrinh> lichTrinhList);
    void deleteById(Integer id);

    /**
     * Thêm nhiều lịch trình cho một tour (SRP: logic này thuộc về service).
     *
     * @param maTour    mã tour cần thêm lịch trình
     * @param requests  danh sách dữ liệu lịch trình từ client
     */
    void addMultiple(Integer maTour, List<LichTrinhRequest> requests);

    /**
     * DTO nội bộ dùng để nhận dữ liệu lịch trình từ client.
     * Đặt tại đây để tránh phụ thuộc giữa controller và repository.
     */
    class LichTrinhRequest {
        private Integer maTour;
        private Integer thuTuNgay;
        private Integer maDiaDiem;
        private String  hoatDong;
        private Integer maPhuongTien;
        private Integer maKhachSan;

        public Integer getMaTour()        { return maTour; }
        public void setMaTour(Integer v)  { this.maTour = v; }
        public Integer getThuTuNgay()     { return thuTuNgay; }
        public void setThuTuNgay(Integer v){ this.thuTuNgay = v; }
        public Integer getMaDiaDiem()     { return maDiaDiem; }
        public void setMaDiaDiem(Integer v){ this.maDiaDiem = v; }
        public String  getHoatDong()      { return hoatDong; }
        public void setHoatDong(String v) { this.hoatDong = v; }
        public Integer getMaPhuongTien()  { return maPhuongTien; }
        public void setMaPhuongTien(Integer v){ this.maPhuongTien = v; }
        public Integer getMaKhachSan()    { return maKhachSan; }
        public void setMaKhachSan(Integer v){ this.maKhachSan = v; }
    }
}

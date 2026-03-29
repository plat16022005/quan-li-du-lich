package com.example.layout.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.*;
import com.example.layout.entity.*;
import com.example.layout.repository.NhanvienRepository;
import com.example.layout.repository.UserRepository;
import com.example.layout.repository.VaiTroRepository;

@Service
public class NhanVienService implements INhanVienService {

    private final NhanvienRepository nhanvienRepository;
    private final VaiTroRepository vaiTroRepository;
    private final UserRepository userRepository;

    public NhanVienService(NhanvienRepository nhanvienRepository,
                           VaiTroRepository vaiTroRepository,
                           UserRepository userRepository) {
        this.nhanvienRepository = nhanvienRepository;
        this.vaiTroRepository = vaiTroRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Nhanvien> getAllNhanVien() {
        return nhanvienRepository.findAll();
    }

    @Override
    public Optional<Nhanvien> findById(Integer maNhanVien) {
        return nhanvienRepository.findById(maNhanVien);
    }

    @Override
    public Nhanvien save(Nhanvien nhanvien) {
        return nhanvienRepository.save(nhanvien);
    }

    @Override
    public void deleteById(Integer maNhanVien) {
        nhanvienRepository.deleteById(maNhanVien);
    }

    @Override
    public Page<Nhanvien> searchStaff(String keyword, Integer role, Boolean status, Pageable pageable) {
        return nhanvienRepository.searchStaff(keyword, role, status, pageable);
    }

    @Override
    public List<Nhanvien> getallHuongDanVien() {
        Vaitro vaiTro = vaiTroRepository.findByTenVaiTro("Hướng dẫn viên")
                .orElseThrow(() -> new RuntimeException("Hướng dẫn viên không tồn tại"));
        return nhanvienRepository.findByTaiKhoan_MaVaiTro(vaiTro.getMaVaiTro());
    }

    @Override
    public List<Nhanvien> getallTaiXe() {
        Vaitro vaiTro = vaiTroRepository.findByTenVaiTro("Tài xế")
                .orElseThrow(() -> new RuntimeException("Tài xế không tồn tại"));
        return nhanvienRepository.findByTaiKhoan_MaVaiTro(vaiTro.getMaVaiTro());
    }

    @Override
    public List<Nhanvien> getAvailableStaff(Vaitro role, LocalDate startDate, LocalDate endDate) {
        return nhanvienRepository.findAvailableStaff(role, startDate, endDate);
    }

    @Override
    public User findTaiKhoanByUsername(String username) {
        return userRepository.findByTenDangNhap(username);
    }

    @Override
    public Nhanvien findByMaTaiKhoan(Integer maTaiKhoan) {
        return nhanvienRepository.findByTaiKhoan_MaTaiKhoan(maTaiKhoan).orElse(null);
    }

    @Override
    public void addStaff(String hoTen, String tenDangNhap, String matKhau, String email, Integer maVaiTro, String soDienThoai, LocalDate ngayVaoLam) {
        if (userRepository.findByTenDangNhap(tenDangNhap) != null) {
            throw new RuntimeException("Tên đăng nhập " + tenDangNhap + " đã tồn tại!");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email " + email + " đã tồn tại!");
        }

        User taiKhoan = new User();
        taiKhoan.setTenDangNhap(tenDangNhap);
        taiKhoan.setMatKhau(matKhau);
        taiKhoan.setHoTen(hoTen);
        taiKhoan.setEmail(email);
        taiKhoan.setSoDienThoai(soDienThoai);
        taiKhoan.setTrangThai(true);
        taiKhoan.setMaVaiTro(maVaiTro);

        userRepository.save(taiKhoan);

        Nhanvien nv = new Nhanvien();
        nv.setTaiKhoan(taiKhoan);
        if (maVaiTro == com.example.layout.utils.VaiTroConstants.QUAN_LY_TOUR) {
            nv.setChucVu("Điều hành Tour");
        } else if (maVaiTro == com.example.layout.utils.VaiTroConstants.HUONG_DAN_VIEN) {
            nv.setChucVu("Hướng dẫn viên");
        } else if (maVaiTro == com.example.layout.utils.VaiTroConstants.TAI_XE) {
            nv.setChucVu("Tài xế");
        }
        nv.setNgayVaoLam(ngayVaoLam);

        nhanvienRepository.save(nv);
    }

    @Override
    public void saveSalary(Integer maNhanVien, java.math.BigDecimal luongCoBan, Integer soNgayLam, java.math.BigDecimal phuCap) {
        Nhanvien nv = nhanvienRepository.findById(maNhanVien).orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        java.math.BigDecimal tongLuong = luongCoBan
            .add(new java.math.BigDecimal(soNgayLam).multiply(new java.math.BigDecimal(200000)))
            .add(phuCap);
        nv.setLuongCoBan(tongLuong);
        nhanvienRepository.save(nv);
    }

    @Override
    public java.math.BigDecimal getCurrentSalary(Integer maNhanVien) {
        Nhanvien nv = nhanvienRepository.findById(maNhanVien).orElse(null);
        if (nv == null || nv.getLuongCoBan() == null) {
            return java.math.BigDecimal.ZERO;
        }
        return nv.getLuongCoBan();
    }

    @Override
    public void deleteStaff(Integer maTaiKhoan) {
        User taiKhoan = userRepository.findById(maTaiKhoan).orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        taiKhoan.setTrangThai(!taiKhoan.getTrangThai());
        userRepository.save(taiKhoan);
    }

    @Override
    public void updateStaff(Integer maNhanVien, String hoTen, String email, String soDienThoai, String chucVu, LocalDate ngayVaoLam) {
        Nhanvien nv = nhanvienRepository.findById(maNhanVien).orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        User tk = nv.getTaiKhoan();
        tk.setHoTen(hoTen);
        tk.setEmail(email);
        tk.setSoDienThoai(soDienThoai);
        userRepository.save(tk);

        nv.setChucVu(chucVu);
        nv.setNgayVaoLam(ngayVaoLam);
        nhanvienRepository.save(nv);
    }
}

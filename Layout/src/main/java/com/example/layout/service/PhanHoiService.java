package com.example.layout.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.layout.entity.KhachHang;
import com.example.layout.entity.PhanHoi;
import com.example.layout.entity.User;
import com.example.layout.repository.PhanHoiRepository;

@Service
public class PhanHoiService implements IPhanHoiService {

    private final PhanHoiRepository phanHoiRepository;

    public PhanHoiService(PhanHoiRepository phanHoiRepository) {
        this.phanHoiRepository = phanHoiRepository;
    }

    @Override
    public List<Map<String, Object>> getFeedbackByCustomer(int customerId) {
        List<PhanHoi> phanHois = phanHoiRepository.findByKhachHangMaKhachHang(customerId);

        List<Map<String, Object>> response = new ArrayList<>();
        for (PhanHoi ph : phanHois) {
            Map<String, Object> item = new HashMap<>();
            item.put("tourName", ph.getChuyenDuLich() != null ? ph.getChuyenDuLich().getTour().getTenTour() : "Tour đã xóa");
            item.put("maChuyen", ph.getChuyenDuLich().getMaChuyen());
            item.put("comment", ph.getNoiDung());
            item.put("rating", ph.getDanhGia());
            item.put("date", ph.getNgayTao() != null ? ph.getNgayTao().toString() : null);
            response.add(item);
        }

        return response;
    }

    @Override
    public Map<String, Object> getCustomersWithFeedback(int page, int size) {
        // Lấy danh sách phản hồi và thông tin khách hàng
        List<PhanHoi> phanHois = phanHoiRepository.findAll();
        Map<Integer, Map<String, Object>> customersMap = new HashMap<>();

        for (PhanHoi ph : phanHois) {
            KhachHang kh = ph.getKhachHang();
            if (kh == null) continue;
            
            Integer id = kh.getMaKhachHang();
            User user = kh.getTaiKhoan();
            if (user == null) continue;

            customersMap.computeIfAbsent(id, k -> {
                Map<String, Object> item = new HashMap<>();
                item.put("customerId", id);
                item.put("name", user.getHoTen());
                item.put("email", user.getEmail());
                item.put("phone", user.getSoDienThoai());
                item.put("address", kh.getDiaChi());
                item.put("joinDate", kh.getNgayThamGia() != null ? kh.getNgayThamGia().toString() : null);
                item.put("totalFeedback", 0);
                return item;
            });

            int count = (int) customersMap.get(id).get("totalFeedback");
            customersMap.get(id).put("totalFeedback", count + 1);
        }

        // Sắp xếp khách hàng theo số lượng phản hồi giảm dần
        List<Map<String, Object>> customers = new ArrayList<>(customersMap.values());
        customers.sort((a, b) -> Integer.compare(
            (int) b.get("totalFeedback"),
            (int) a.get("totalFeedback")
        ));

        // Phân trang
        int totalElements = customers.size();
        int start = page * size;
        int end = Math.min(start + size, totalElements);

        // Kiểm tra và điều chỉnh chỉ số trang nếu cần
        if (totalElements > 0 && start >= totalElements) {
            page = (totalElements - 1) / size;
            start = page * size;
            end = Math.min(start + size, totalElements);
        }

        List<Map<String, Object>> paged = customers.subList(start, end);

        // Gói dữ liệu trả về
        Map<String, Object> response = new HashMap<>();
        response.put("content", paged);
        response.put("totalElements", totalElements);
        response.put("totalPages", (int) Math.ceil((double) totalElements / size));
        response.put("page", page);
        response.put("size", size);
        response.put("empty", totalElements == 0);
        response.put("first", page == 0);
        response.put("last", (page + 1) * size >= totalElements);

        return response;
    }
}

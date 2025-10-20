package com.example.layout.controller.manager.tour;

import com.example.layout.entity.User;
import com.example.layout.repository.ChuyenDuLichRepository;
import com.example.layout.entity.Tour;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.LichTrinh;
import com.example.layout.service.TourService;
import com.example.layout.service.ChuyenDuLichService;
import com.example.layout.service.LichTrinhService;
import com.example.layout.service.NhanVienService;

import jakarta.servlet.http.HttpSession;

import org.springdoc.core.converters.models.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
@Controller
@RequestMapping("/manager")
public class ManagerTourController {

    private final TourService tourService;
    private final ChuyenDuLichService chuyenDuLichService;
    private final NhanVienService nhanVienService;
    private final LichTrinhService lichTrinhService;

    public ManagerTourController(TourService tourService, ChuyenDuLichService chuyenDuLichService, NhanVienService nhanVienService, LichTrinhService lichTrinhService) {
        this.tourService = tourService;
        this.chuyenDuLichService = chuyenDuLichService;
        this.nhanVienService = nhanVienService;
        this.lichTrinhService = lichTrinhService;
    }

    @GetMapping("/tour")
    public String showTourForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        // Cho phép Admin (1) hoặc Quản lý tour (2)
        if (user == null || (user.getMaVaiTro() != 1 && user.getMaVaiTro() != 2)) {
            return "redirect:/access_denied";
        }

        List<Tour> tours = tourService.getAllTours();
        Map<Integer, ChuyenDuLich> nearestChuyens = new HashMap<>();
        Map<Integer, String> participantCounts = new HashMap<>();

        for (Tour tour : tours) {
            chuyenDuLichService.getNearestChuyen(tour.getMaTour()).ifPresent(chuyen -> {
                nearestChuyens.put(tour.getMaTour(), chuyen);
                int soNguoi = chuyenDuLichService.getTotalParticipants(chuyen.getMaChuyen());
                int max = chuyen.getSoLuongToiDa();
                participantCounts.put(tour.getMaTour(), soNguoi + "/" + max);
            });
        }

        model.addAttribute("tours", tours);
        model.addAttribute("nearestChuyens", nearestChuyens);
        model.addAttribute("participantCounts", participantCounts);
        model.addAttribute("currentUser", user);
        return "manager/tour";
    }

    @Autowired
    private com.example.layout.repository.DatChoRepository datChoRepository;

    @GetMapping("/tour/{maTour}/bookings")
    @ResponseBody
    public ResponseEntity<?> getBookingsByTour(@PathVariable Integer maTour, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || (user.getMaVaiTro() != 1 && user.getMaVaiTro() != 2)) {
            return ResponseEntity.status(403).body("Không có quyền");
        }

        List<com.example.layout.entity.DatCho> bookings = datChoRepository.findByChuyenDuLich_Tour_MaTour(maTour);

        // Map to lightweight DTO
        List<java.util.Map<String, Object>> response = bookings.stream().map(dc -> {
            java.util.Map<String, Object> m = new java.util.HashMap<>();
            m.put("maDatCho", dc.getMaDatCho());
            if (dc.getKhachHang() != null && dc.getKhachHang().getTaiKhoan() != null) {
                m.put("tenKhach", dc.getKhachHang().getTaiKhoan().getHoTen());
                m.put("email", dc.getKhachHang().getTaiKhoan().getEmail());
                m.put("phone", dc.getKhachHang().getTaiKhoan().getSoDienThoai());
            } else {
                m.put("tenKhach", "Khách ẩn");
                m.put("email", "");
                m.put("phone", "");
            }
            m.put("ngayDat", dc.getNgayDat());
            int total = dc.getChiTietDatChos() == null ? 0 : dc.getChiTietDatChos().stream().mapToInt(ct -> ct.getSoLuong() == null ? 0 : ct.getSoLuong()).sum();
            m.put("soLuong", total);
            return m;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/tour/upload-image")
    @ResponseBody
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String uploadDir = "uploads";
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/uploads/" + fileName;
            return ResponseEntity.ok(imageUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi upload ảnh");
        }
    }

    @PostMapping("/tour/add")
    public String addTourAndChuyen(
            @RequestParam String tenTour,
            @RequestParam BigDecimal giaCoBan,
            @RequestParam int soNgay,
            @RequestParam(required = false) String moTa,
            @RequestParam("file") MultipartFile file,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayBatDau,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayKetThuc,
            @RequestParam int soLuongToiDa,
            @RequestParam BigDecimal giaThueHDV,
            @RequestParam BigDecimal giaThueTX,
            @RequestParam String trangThai,
            RedirectAttributes redirectAttributes
    ) {
        try {
            String uploadDir = "uploads";
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/uploads/" + fileName;

            Tour tour = new Tour();
            tour.setTenTour(tenTour);
            tour.setGiaCoBan(giaCoBan);
            tour.setSoNgay(soNgay);
            tour.setMoTa(moTa);
            tour.setHinhAnh(imageUrl);
            Tour savedTour = tourService.saveTour(tour);

            ChuyenDuLich chuyen = new ChuyenDuLich();
            chuyen.setTour(savedTour);
            chuyen.setNgayBatDau(ngayBatDau);
            chuyen.setNgayKetThuc(ngayKetThuc);
            chuyen.setSoLuongToiDa(soLuongToiDa);
            chuyen.setTrangThai(trangThai);
            chuyen.setGiaThueHDV(giaThueHDV);
            chuyen.setGiaThueTX(giaThueTX);
            chuyenDuLichService.saveChuyen(chuyen);

            redirectAttributes.addFlashAttribute("success", "Thêm tour và chuyến đi thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/manager/tour";
    }

    @DeleteMapping("/tour/deletetour/{maTour}")
    public ResponseEntity<?> deleteTour(@PathVariable Integer maTour) {
        try {
            tourService.deleteTourById(maTour);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi khi xóa tour");
        }
    }

    @GetMapping("/tour/get/{maTour}")
    @ResponseBody
    public ResponseEntity<?> getTourAndChuyen(@PathVariable Integer maTour) {
        Tour tour = tourService.getTourById(maTour);
        ChuyenDuLich chuyen = chuyenDuLichService.getNearestChuyen(maTour).orElse(null);

        Map<String, Object> response = new HashMap<>();
        response.put("tour", tour);
        response.put("chuyen", chuyen);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/tour/update/{maTour}")
    @ResponseBody
    public ResponseEntity<?> updateTourAndChuyen(
            @PathVariable Integer maTour,
            @org.springframework.web.bind.annotation.RequestBody Map<String, Object> body) {  // ✅ ĐÃ ĐỔI annotation

        try {
            Map<String, Object> tourData = (Map<String, Object>) body.get("tour");
            Map<String, Object> chuyenData = (Map<String, Object>) body.get("chuyen");

            Tour tour = tourService.getTourById(maTour);
            if (tour == null) return ResponseEntity.notFound().build();

            tour.setTenTour((String) tourData.get("tenTour"));
            tour.setGiaCoBan(new BigDecimal(tourData.get("giaCoBan").toString()));
            tour.setSoNgay(Integer.parseInt(tourData.get("soNgay").toString()));
            tour.setMoTa((String) tourData.get("moTa"));
            tour.setHinhAnh((String) tourData.get("hinhAnh"));
            System.out.println(">>>> HINH ANH NHẬN VÀO: " + tourData.get("hinhAnh"));
            tourService.saveTour(tour);

            if (chuyenData != null && chuyenData.get("maChuyen") != null) {
                Integer maChuyen = Integer.parseInt(chuyenData.get("maChuyen").toString());
                ChuyenDuLich chuyen = chuyenDuLichService.getChuyenById(maChuyen);
                if (chuyen != null) {
                    chuyen.setNgayBatDau(LocalDate.parse(chuyenData.get("ngayBatDau").toString()));
                    chuyen.setNgayKetThuc(LocalDate.parse(chuyenData.get("ngayKetThuc").toString()));
                    chuyen.setSoLuongToiDa(Integer.parseInt(chuyenData.get("soLuongToiDa").toString()));
                    chuyen.setTrangThai((String) chuyenData.get("trangThai"));
                    chuyenDuLichService.saveChuyen(chuyen);
                }
            }

            return ResponseEntity.ok("Cập nhật Tour và Chuyến thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi khi cập nhật Tour & Chuyến");
        }
    }
    @GetMapping("/tour/search")
    @ResponseBody
    public ResponseEntity<?> searchTours(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {

        // Lấy tất cả tour
        List<Tour> tours = tourService.getAllTours();
        Map<Integer, ChuyenDuLich> nearestChuyens = new HashMap<>();

        // Lọc chuyến gần nhất
        for (Tour tour : tours) {
            chuyenDuLichService.getNearestChuyen(tour.getMaTour()).ifPresent(chuyen -> {
                nearestChuyens.put(tour.getMaTour(), chuyen);
            });
        }

        // Lọc theo keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lower = keyword.toLowerCase();
            tours = tours.stream()
                    .filter(t -> t.getTenTour().toLowerCase().contains(lower))
                    .toList();
        }

        // Lọc theo trạng thái
        if (status != null && !status.equalsIgnoreCase("Tất cả trạng thái")) {
            String lowerStatus = status.toLowerCase();
            tours = tours.stream()
                    .filter(t -> {
                        ChuyenDuLich chuyen = nearestChuyens.get(t.getMaTour());
                        return chuyen != null && chuyen.getTrangThai().toLowerCase().contains(lowerStatus);
                    })
                    .toList();
        }

        // Tạo response JSON
        List<Map<String, Object>> response = tours.stream().map(t -> {
            Map<String, Object> map = new HashMap<>();
            map.put("maTour", t.getMaTour());
            map.put("tenTour", t.getTenTour());
            map.put("giaCoBan", t.getGiaCoBan());
            map.put("soNgay", t.getSoNgay());
            map.put("hinhAnh", t.getHinhAnh());

            ChuyenDuLich chuyen = nearestChuyens.get(t.getMaTour());
            if (chuyen != null) {
                map.put("ngayBatDau", chuyen.getNgayBatDau());
                map.put("trangThai", chuyen.getTrangThai());

                // 🧮 TÍNH SỐ KHÁCH
                int soNguoi = chuyenDuLichService.getTotalParticipants(chuyen.getMaChuyen());
                int max = chuyen.getSoLuongToiDa();
                map.put("soKhach", soNguoi + "/" + max);

            } else {
                map.put("ngayBatDau", "Chưa có chuyến");
                map.put("trangThai", "Chưa có chuyến");
                map.put("soKhach", "-");
            }
            return map;
        }).toList();

        return ResponseEntity.ok(response);
    }
    @GetMapping("/tour/detail/{maTour}")
    public String showTourDetailForm(@PathVariable("maTour") String maTour,HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if (user == null || (user.getMaVaiTro() != 1 && user.getMaVaiTro() != 2)) {
            return "redirect:/access_denied";
        }
    	session.setAttribute("matour", maTour);
    	return "manager/tour_detail";
    }

    @GetMapping("/tour/{maTour}/create-trip")
    public String createTrip(@PathVariable("maTour") String maTour, Model model, HttpSession session) {
        Tour tour = tourService.getTourById(Integer.parseInt(maTour));
        if (tour == null) {
            return "redirect:/manager/tour";
        }

        User user = (User) session.getAttribute("user");
        if (user == null || (user.getMaVaiTro() != 1 && user.getMaVaiTro() != 2)) {
            return "redirect:/access_denied";
        }

        ChuyenDuLich chuyenDuLich = new ChuyenDuLich();
        chuyenDuLich.setTour(tour);

        model.addAttribute("tour", tour);
        model.addAttribute("chuyenDuLich", chuyenDuLich);
        model.addAttribute("hdv", nhanVienService.getallHuongDanVien());
        model.addAttribute("tx", nhanVienService.getallTaiXe());    

        return "manager/create_trip";
    }

    @GetMapping("tour/{maTour}/trips")
    public String showAllTrips(@PathVariable("maTour") String maTour, Model model,HttpSession session) {
        Tour tour = tourService.getTourById(Integer.parseInt(maTour));
        if (tour == null) {
            return "redirect:/manager/tour";
        }

        User user = (User) session.getAttribute("user");
        if (user == null || (user.getMaVaiTro() != 1 && user.getMaVaiTro() != 2)) {
            return "redirect:/access_denied";
        }

        List<ChuyenDuLich> chuyenDuLichList = chuyenDuLichService.findByTour_MaTour(Integer.parseInt(maTour));
        
        model.addAttribute("tour", tour);
        model.addAttribute("chuyenDuLichList", chuyenDuLichList);
        return "manager/show_all_trips";
    }
    

    @GetMapping("/tour/trips/{maChuyen}")
    public String showTripDetailPage(@PathVariable("maChuyen") Integer maChuyen, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || (user.getMaVaiTro() != 1 && user.getMaVaiTro() != 2)) {
            return "redirect:/access_denied";
        }

        ChuyenDuLich chuyen = chuyenDuLichService.getChuyenById(maChuyen);
        if (chuyen == null) {
            return "redirect:/manager/tour"; 
        }

        List<LichTrinh> lichTrinhList = lichTrinhService.getLichTrinhByTour(chuyen.getTour().getMaTour());

        model.addAttribute("chuyen", chuyen);
        model.addAttribute("lichTrinhList", lichTrinhList);

        return "manager/trip_detail";
    }

    @GetMapping("/tour/trips/edit/{maChuyen}")
    public String showEditTripForm(@PathVariable("maChuyen") Integer maChuyen, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || (user.getMaVaiTro() != 1 && user.getMaVaiTro() != 2)) {
            return "redirect:/access_denied";
        }

        ChuyenDuLich chuyen = chuyenDuLichService.getChuyenById(maChuyen);
        if (chuyen == null) {
            return "redirect:/manager/tour";
        }
        
        if (chuyen != null) {
            System.out.println(">>> DEBUG CONTROLLER: Ngày bắt đầu lấy từ DB là: " + chuyen.getNgayBatDau());
            System.out.println(">>> DEBUG CONTROLLER: Ngày kết thúc lấy từ DB là: " + chuyen.getNgayKetThuc());
            // Kiểm tra luôn kiểu dữ liệu
            if(chuyen.getNgayBatDau() != null) {
                System.out.println(">>> KIỂU DỮ LIỆU: " + chuyen.getNgayBatDau().getClass().getName());
            }
        }

        model.addAttribute("chuyenDuLich", chuyen); 
        model.addAttribute("tour", chuyen.getTour());
        model.addAttribute("hdv", nhanVienService.getallHuongDanVien());
        model.addAttribute("tx", nhanVienService.getallTaiXe());
        
        return "manager/create_trip";
    }
    
    @GetMapping("/tour/trips/delete/{maChuyen}")
    public String deleteTrip(@PathVariable("maChuyen") Integer maChuyen, RedirectAttributes redirectAttributes) {
        // Lấy maTour để biết đường quay lại trang danh sách chuyến
        ChuyenDuLich chuyen = chuyenDuLichService.getChuyenById(maChuyen);
        Integer maTour = (chuyen != null) ? chuyen.getTour().getMaTour() : null;

        try {
            chuyenDuLichService.deleteById(maChuyen);
            redirectAttributes.addFlashAttribute("success", "Xóa chuyến đi thành công!");
        } catch (Exception e) {
            // Xử lý lỗi nếu chuyến đi đã có khách đặt, không thể xóa
            redirectAttributes.addFlashAttribute("error", "Không thể xóa chuyến đi này vì đã có dữ liệu liên quan.");
        }
        
        // Quay lại trang danh sách chuyến của tour đó, hoặc trang tour chính nếu không lấy được maTour
        return "redirect:" + (maTour != null ? "/manager/tour/" + maTour + "/trips" : "/manager/tour");
    }

    @PostMapping("/tour/create-trip-post")
    public String handleSaveChuyenDuLich(@ModelAttribute("chuyenDuLich") ChuyenDuLich chuyenDuLich, 
                                        RedirectAttributes redirectAttributes) {
        try {
            chuyenDuLichService.saveChuyen(chuyenDuLich);
            redirectAttributes.addFlashAttribute("success", "Lưu chuyến đi thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi lưu chuyến đi.");
        }
        
        Integer maTour = chuyenDuLich.getTour().getMaTour();
        return "redirect:/manager/tour/" + maTour + "/trips";
    }

    
}

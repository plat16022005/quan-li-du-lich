package com.example.layout.controller.nhanvien;

import com.example.layout.dto.TourDTO;
import com.example.layout.entity.ChuyenDuLich;
import com.example.layout.entity.Tour;
import com.example.layout.entity.User;
import com.example.layout.service.ChuyenDuLichService;
import com.example.layout.service.NhanVienService;
import com.example.layout.service.TourService;

import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/nhanvien")
public class NhanvienTourController {

    private final TourService tourService;
    private final ChuyenDuLichService chuyenDuLichService;
    private final NhanVienService nhanVienService;

    public NhanvienTourController(TourService tourService, ChuyenDuLichService chuyenDuLichService, NhanVienService nhanVienService) {
        this.tourService = tourService;
        this.chuyenDuLichService = chuyenDuLichService;
        this.nhanVienService = nhanVienService;
    }

    @PostMapping("/tour/add")
    public String addTourAndChuyen(
            @RequestParam String tenTour,
            @RequestParam BigDecimal giaCoBan,
            @RequestParam int soNgay,
            @RequestParam(required = false) String mota,
            @RequestParam("file") MultipartFile file,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        // Kiểm tra quyền truy cập
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 2) {
            return "redirect:/access_denied";
        }

        try {
            // Tạo thư mục uploads nếu chưa tồn tại
            String uploadDir = "uploads";
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            // Lưu file ảnh
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/uploads/" + fileName;

            // Tạo Tour
            Tour tour = new Tour();
            tour.setTenTour(tenTour);
            tour.setGiaCoBan(giaCoBan);
            tour.setSoNgay(soNgay);
            tour.setMoTa(mota);
            tour.setHinhAnh(imageUrl);
            Tour savedTour = tourService.saveTour(tour);

            redirectAttributes.addFlashAttribute("success", "Thêm tour và chuyến đi thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/nhanvien/manager_tour";
    }

    @GetMapping("/manager_tour/get/{maTour}")
    @ResponseBody
    public ResponseEntity<?> getTourAndChuyen(@PathVariable Integer maTour) {
        Tour tour = tourService.getTourById(maTour);
        //ChuyenDuLich chuyen = chuyenDuLichService.getNearestChuyen(maTour).orElse(null);

        // Map<String, Object> response = new HashMap<>();
        // response.put("tour", tour);
        // response.put("chuyen", chuyen);

        TourDTO tourDTO = new TourDTO(
                tour.getMaTour(),
                tour.getTenTour(),
                tour.getGiaCoBan(),
                tour.getSoNgay(),
                tour.getMoTa(),
                tour.getHinhAnh()
        );

        return ResponseEntity.ok(tourDTO);
    }

    @PutMapping("/manager_tour/update/{maTour}")
    @ResponseBody
    public ResponseEntity<?> updateTourAndChuyen(
            @PathVariable Integer maTour,
            @org.springframework.web.bind.annotation.RequestBody Map<String, Object> body) {  // ✅ ĐÃ ĐỔI annotation

        try {
            Map<String, Object> tourData = (Map<String, Object>) body.get("tour");
            // Map<String, Object> chuyenData = (Map<String, Object>) body.get("chuyen");
            Tour tour = tourService.getTourById(maTour);
            if (tour == null) return ResponseEntity.notFound().build();

            tour.setTenTour((String) tourData.get("tenTour"));
            tour.setGiaCoBan(new BigDecimal(tourData.get("giaCoBan").toString()));
            tour.setSoNgay(Integer.parseInt(tourData.get("soNgay").toString()));
            tour.setMoTa((String) tourData.get("moTa"));
            tour.setHinhAnh((String) tourData.get("hinhAnh"));
            System.out.println(">>>> HINH ANH NHẬN VÀO: " + tourData.get("hinhAnh"));
            tourService.saveTour(tour);

            // if (chuyenData != null && chuyenData.get("maChuyen") != null) {
            //     Integer maChuyen = Integer.parseInt(chuyenData.get("maChuyen").toString());
            //     ChuyenDuLich chuyen = chuyenDuLichService.getChuyenById(maChuyen);
            //     if (chuyen != null) {
            //         chuyen.setNgayBatDau(LocalDate.parse(chuyenData.get("ngayBatDau").toString()));
            //         chuyen.setNgayKetThuc(LocalDate.parse(chuyenData.get("ngayKetThuc").toString()));
            //         chuyen.setSoLuongToiDa(Integer.parseInt(chuyenData.get("soLuongToiDa").toString()));
            //         chuyen.setTrangThai((String) chuyenData.get("trangThai"));
            //         chuyenDuLichService.saveChuyen(chuyen);
            //     }

            return ResponseEntity.ok("Cập nhật Tour và Chuyến thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi khi cập nhật Tour & Chuyến");
        }
    }

    @PostMapping("/tour/upload-image")
    @ResponseBody
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file, HttpSession session) {
        // Kiểm tra quyền truy cập
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 2) {
            return ResponseEntity.status(403).body("Không có quyền truy cập");
        }

        try {
            String uploadDir = "uploads";
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/uploads/" + fileName;
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi upload ảnh: " + e.getMessage());
        }
    }
    @GetMapping("/manager_tour/{maTour}/create-trip")
    public String createTrip(@PathVariable("maTour") String maTour, Model model, HttpSession session) {
        Tour tour = tourService.getTourById(Integer.parseInt(maTour));
        if (tour == null) {
            return "redirect:/nhanvien/manager_tour";
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

        return "nhanvien/create_trip";
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
        return "redirect:/nhanvien/manager_tour/" + maTour + "/trips";
    }
    @GetMapping("manager_tour/{maTour}/trips")
    public String showAllTrips(@PathVariable("maTour") String maTour, Model model,HttpSession session) {
        Tour tour = tourService.getTourById(Integer.parseInt(maTour));
        if (tour == null) {
            return "redirect:/nhanvien/manager_tour";
        }

        User user = (User) session.getAttribute("user");
        if (user == null || (user.getMaVaiTro() != 1 && user.getMaVaiTro() != 2)) {
            return "redirect:/access_denied";
        }

        List<ChuyenDuLich> chuyenDuLichList = chuyenDuLichService.findByTour_MaTour(Integer.parseInt(maTour));
        
        model.addAttribute("tour", tour);
        model.addAttribute("chuyenDuLichList", chuyenDuLichList);
        return "nhanvien/show_all_trips";
    }
    @GetMapping("/manager_tour/detail/{maTour}")
    public String showTourDetailForm(@PathVariable("maTour") String maTour,HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if (user == null || (user.getMaVaiTro() != 1 && user.getMaVaiTro() != 2)) {
            return "redirect:/access_denied";
        }
    	session.setAttribute("matour", maTour);
    	return "nhanvien/tour_detail";
    }

}
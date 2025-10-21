package com.example.layout.controller.nhanvien;

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
            @RequestParam(required = false) String diaDiem,
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
            tour.setMoTa(diaDiem);
            tour.setHinhAnh(imageUrl);
            Tour savedTour = tourService.saveTour(tour);

            redirectAttributes.addFlashAttribute("success", "Thêm tour và chuyến đi thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/nhanvien/manager_tour";
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
    @GetMapping("/tour/{maTour}/create-trip")
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
}
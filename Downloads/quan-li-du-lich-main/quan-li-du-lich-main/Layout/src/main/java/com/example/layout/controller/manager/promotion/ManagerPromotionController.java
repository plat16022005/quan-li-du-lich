package com.example.layout.controller.manager.promotion;

//import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.layout.entity.KhuyenMai;
import com.example.layout.entity.User;
import com.example.layout.repository.KhuyenMaiRepository;
import com.example.layout.service.KhuyenMaiService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/manager")
public class ManagerPromotionController {

    @Autowired
    private KhuyenMaiService khuyenMaiService;
    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;

    // ✅ Trang danh sách khuyến mãi (kiểm tra quyền truy cập)
    @GetMapping("/promotion")
    public String showPromotionForm(HttpSession session,
                                    Model model,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size) {

        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 1) {
            return "redirect:/access-denied";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<KhuyenMai> promotionsPage = khuyenMaiRepository.findAll(pageable);

        model.addAttribute("promotions", promotionsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", promotionsPage.getTotalPages());
        model.addAttribute("totalItems", promotionsPage.getTotalElements());
        model.addAttribute("pageSize", size);

        return "manager/promotion";
    }

    // ✅ Thêm khuyến mãi
    @PostMapping("/promotion/add")
    public String addPromotion(HttpSession session, @ModelAttribute KhuyenMai km) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 1) {
            return "redirect:/access-denied";
        }

        khuyenMaiService.save(km);
        return "redirect:/manager/promotion";
    }

    // ✅ Cập nhật khuyến mãi
    @PostMapping("/promotion/update")
    public String updatePromotion(HttpSession session, @ModelAttribute KhuyenMai km) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 1) {
            return "redirect:/access-denied";
        }

        khuyenMaiService.save(km);
        return "redirect:/manager/promotion";
    }

    // ✅ Xóa khuyến mãi
    @GetMapping("/promotion/delete/{id}")
    public String deletePromotion(HttpSession session, @PathVariable Integer id) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 1) {
            return "redirect:/access-denied";
        }

        khuyenMaiService.deleteById(id);
        return "redirect:/manager/promotion";
    }

    // ✅ Lấy thông tin khuyến mãi để chỉnh sửa (AJAX)
    @GetMapping("/promotion/edit/{id}")
    @ResponseBody
    public KhuyenMai getPromotionById(HttpSession session, @PathVariable Integer id) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getMaVaiTro() != 1) {
            return null;
        }

        return khuyenMaiService.getById(id);
    }
}

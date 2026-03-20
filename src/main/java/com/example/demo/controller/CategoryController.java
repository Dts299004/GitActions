package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    // ========== SEED DATA ==========
    @PostConstruct
    public void initData() {
        // Tự động nhận diện và cập nhật icon cho các danh mục hiện có nếu chúng chưa có hoặc icon rỗng
        List<Category> allCategories = categoryRepository.findAll();
        for (Category c : allCategories) {
            if (c.getParent() == null) {
                String name = c.getName().toLowerCase();
                String icon = c.getIcon();
                if (icon == null || icon.trim().isEmpty() || icon.equals("fas fa-folder")) {
                    if (name.contains("điện thoại")) c.setIcon("fas fa-mobile-alt");
                    else if (name.contains("laptop")) c.setIcon("fas fa-laptop");
                    else if (name.contains("đồng hồ") || name.contains("smartwatch")) c.setIcon("fas fa-stopwatch");
                    else if (name.contains("ổ cứng") || name.contains("lưu trữ")) c.setIcon("fas fa-hdd");
                    else if (name.contains("nghe nhạc") || name.contains("âm thanh")) c.setIcon("fas fa-headphones-alt");
                    else if (name.contains("camera")) c.setIcon("fas fa-camera");
                    else if (name.contains("tablet") || name.contains("máy tính bảng")) c.setIcon("fas fa-tablet-alt");
                    else if (name.contains("chuột") || name.contains("phím")) c.setIcon("fas fa-keyboard");
                    else c.setIcon("fas fa-folder");
                    categoryRepository.save(c);
                }
            }
        }

        if (categoryRepository.count() > 0) return;
        seedAccessories();
    }

    public void seedAccessories() {

        // ==== PHỤ KIỆN DI ĐỘNG ====
        Category pk1 = parent("Phụ kiện di động", "fas fa-mobile-alt");
        child(pk1, "Sạc dự phòng",           "https://picsum.photos/seed/powerbank/100/100");
        child(pk1, "Sạc, cáp",               "https://picsum.photos/seed/charger/100/100");
        child(pk1, "Ốp lưng điện thoại",     "https://picsum.photos/seed/phonecase/100/100");
        child(pk1, "Ốp lưng máy tính bảng",  "https://picsum.photos/seed/tabletcase/100/100");
        child(pk1, "Miếng dán",               "https://picsum.photos/seed/screenfilm/100/100");
        child(pk1, "Miếng dán Camera",        "https://picsum.photos/seed/cameralens/100/100");
        child(pk1, "Túi đựng AirPods",        "https://picsum.photos/seed/airpods/100/100");
        child(pk1, "AirTag, Vỏ bảo vệ...",   "https://picsum.photos/seed/airtag/100/100");
        child(pk1, "Bút tablet",              "https://picsum.photos/seed/stylus/100/100");
        child(pk1, "Giá đỡ điện thoại/laptop...", "https://picsum.photos/seed/phonestand/100/100");
        child(pk1, "Dây đeo điện thoại",      "https://picsum.photos/seed/phonestrap/100/100");
        child(pk1, "Ống kính điện thoại",     "https://picsum.photos/seed/phonelens/100/100");

        // ==== PHỤ KIỆN LAPTOP, PC ====
        Category pk2 = parent("Phụ kiện laptop, PC", "fas fa-laptop");
        child(pk2, "Hub, cáp chuyển đổi",    "https://picsum.photos/seed/usbhub/100/100");
        child(pk2, "Chuột máy tính",          "https://picsum.photos/seed/mouse/100/100");
        child(pk2, "Bàn phím",                "https://picsum.photos/seed/keyboard/100/100");
        child(pk2, "Router - Thiết bị...",    "https://picsum.photos/seed/router/100/100");
        child(pk2, "Balo, túi chống sốc",     "https://picsum.photos/seed/laptopbag/100/100");
        child(pk2, "Túi đựng phụ kiện",       "https://picsum.photos/seed/gearpouch/100/100");
        child(pk2, "Phủ phím laptop",          "https://picsum.photos/seed/keyboardcover/100/100");
        child(pk2, "Phần mềm",                 "https://picsum.photos/seed/software/100/100");
        child(pk2, "Giá treo màn hình",        "https://picsum.photos/seed/monitorarm/100/100");
        child(pk2, "Đèn thông minh/livestream","https://picsum.photos/seed/ringlight/100/100");
        child(pk2, "Miếng lót chuột",          "https://picsum.photos/seed/mousepad/100/100");
        child(pk2, "Bảng vẽ điện tử",          "https://picsum.photos/seed/drawingtablet/100/100");

        // ==== THIẾT BỊ ÂM THANH ====
        Category pk3 = parent("Thiết bị âm thanh", "fas fa-volume-up");
        child(pk3, "Tai nghe Bluetooth",       "https://picsum.photos/seed/bluetoothearbuds/100/100");
        child(pk3, "Tai nghe dây",             "https://picsum.photos/seed/wiredheadphones/100/100");
        child(pk3, "Tai nghe chụp tai",        "https://picsum.photos/seed/overearheadphones/100/100");
        child(pk3, "Tai nghe thể thao",        "https://picsum.photos/seed/sportheadphones/100/100");
        child(pk3, "Loa",                      "https://picsum.photos/seed/bluetoothspeaker/100/100");
        child(pk3, "Micro",                    "https://picsum.photos/seed/microphone/100/100");

        // ==== CAMERA ====
        Category pk4 = parent("Camera", "fas fa-camera");
        child(pk4, "Camera Giám Sát",          "https://picsum.photos/seed/securitycam/100/100");
        child(pk4, "Camera trong nhà",         "https://picsum.photos/seed/indoorcam/100/100");
        child(pk4, "Camera ngoài trời",        "https://picsum.photos/seed/outdoorcam/100/100");
        child(pk4, "Camera hành trình...",     "https://picsum.photos/seed/dashcam/100/100");
        child(pk4, "Camera Năng Lượng...",     "https://picsum.photos/seed/solarcam/100/100");
        child(pk4, "Camera 4G",                "https://picsum.photos/seed/4gcamera/100/100");

        // ==== PHỤ KIỆN GAMING ====
        Category pk5 = parent("Phụ kiện gaming", "fas fa-gamepad");
        child(pk5, "Chuột gaming",             "https://picsum.photos/seed/gamingmouse/100/100");
        child(pk5, "Bàn phím gaming",           "https://picsum.photos/seed/gamingkeyboard/100/100");
        child(pk5, "Tai nghe gaming",           "https://picsum.photos/seed/gamingheadset/100/100");
        child(pk5, "Pad chuột gaming",          "https://picsum.photos/seed/gamingpad/100/100");
        child(pk5, "Tay cầm chơi game",         "https://picsum.photos/seed/gamecontroller/100/100");

        // ==== THIẾT BỊ LƯU TRỮ ====
        Category pk6 = parent("Thiết bị lưu trữ", "fas fa-hdd");
        child(pk6, "Ổ cứng di động",           "https://picsum.photos/seed/portablehdd/100/100");
        child(pk6, "Thẻ nhớ",                  "https://picsum.photos/seed/memorycard/100/100");
        child(pk6, "USB",                       "https://picsum.photos/seed/usb/100/100");
        child(pk6, "Ổ cứng SSD",               "https://picsum.photos/seed/ssdrive/100/100");
    }

    private Category parent(String name, String icon) {
        Category c = new Category();
        c.setName(name);
        c.setIcon(icon);
        return categoryRepository.save(c);
    }

    private void child(Category p, String name, String image) {
        Category c = new Category();
        c.setName(name);
        c.setImage(image);
        c.setParent(p);
        categoryRepository.save(c);
    }

    // ========== TRANG PHỤ KIỆN FULL PAGE ==========
    @GetMapping("/accessories")
    public String showAccessories(Model model) {
        List<Category> parents = categoryRepository.findByParentIsNull();
        model.addAttribute("parentCategories", parents);
        return "accessories";
    }

    // ========== FORM THÊM DANH MỤC ==========
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("parentCategories", categoryRepository.findByParentIsNull());
        return "category-form";
    }

    @PostMapping("/add")
    public String addCategory(@Valid Category category,
                              BindingResult result,
                              @RequestParam(value = "parentId", required = false) Long parentId,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("parentCategories", categoryRepository.findByParentIsNull());
            return "category-form";
        }
        if (parentId != null) {
            categoryRepository.findById(parentId).ifPresent(category::setParent);
        }
        categoryRepository.save(category);
        return "redirect:/categories/accessories";
    }

    // ========== FORM SỬA DANH MỤC ==========
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid category Id: " + id));
        model.addAttribute("category", category);
        model.addAttribute("parentCategories", categoryRepository.findByParentIsNull());
        return "category-form";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable("id") Long id, @Valid Category category,
                                 BindingResult result,
                                 @RequestParam(value = "parentId", required = false) Long parentId,
                                 Model model) {
        if (result.hasErrors()) {
            category.setId(id);
            model.addAttribute("parentCategories", categoryRepository.findByParentIsNull());
            return "category-form";
        }
        if (parentId != null) {
            categoryRepository.findById(parentId).ifPresent(category::setParent);
        } else {
            category.setParent(null);
        }
        categoryRepository.save(category);
        return "redirect:/categories/accessories";
    }

    // ========== XOÁ DANH MỤC ==========
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, Model model) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid category Id: " + id));
        // Xoá con trước khi xoá cha nếu cần, nhưng cascade = CascadeType.ALL đã xử lý
        categoryRepository.delete(category);
        return "redirect:/categories/accessories";
    }
}

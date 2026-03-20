package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PointController {

    private final UserService userService;

    @GetMapping("/points")
    public String showPointsPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            User user = userService.findByUsername(auth.getName()).orElse(null);
            model.addAttribute("user", user);
            return "users/points";
        }
        return "redirect:/login";
    }

    @PostMapping("/points/exchange")
    public String exchangePoints(@RequestParam("amount") int amount, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            User user = userService.findByUsername(auth.getName()).orElse(null);
            if (user != null) {
                if (user.getPoints() >= amount) {
                    user.setPoints(user.getPoints() - amount);
                    userService.updateUserInfo(user);
                    model.addAttribute("success", "Đổi " + amount + " điểm thành công!");
                } else {
                    model.addAttribute("error", "Tích lũy của bạn không đủ " + amount + " điểm để đổi!");
                }
                model.addAttribute("user", user);
            }
            return "users/points";
        }
        return "redirect:/login";
    }
}

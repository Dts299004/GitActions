package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "redirect:/products";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "403";
    }
}

package com.example.demo.controller;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
@Controller 
@RequestMapping("/")
@RequiredArgsConstructor 
public class UserController {
 private final UserService userService;
 @GetMapping("/login") 
 public String login() {
 return "users/login";
 }
 @GetMapping("/register") 
 public String register(@NotNull Model model) {
 model.addAttribute("user", new User()); 
 return "users/register"; 
 }
 @PostMapping("/register") 
 public String register(@Valid @ModelAttribute("user") User user, 
 @NotNull BindingResult bindingResult, 
 @RequestParam("roleName") String roleName,
 Model model) {
 if (bindingResult.hasErrors()) { 
 var errors = bindingResult.getAllErrors() 
 .stream()
 .map(DefaultMessageSourceResolvable::getDefaultMessage)
 .toArray(String[]::new); 
 model.addAttribute("errors", errors);
 return "users/register"; 
 }
 if (userService.findByUsername(user.getUsername()).isPresent()) {
 model.addAttribute("errors", new String[] {"Tên đăng nhập đã tồn tại!"});
 return "users/register";
 }
 if (userService.existsByEmail(user.getEmail())) {
 model.addAttribute("errors", new String[] {"Email đã được sử dụng!"});
 return "users/register";
 }
 userService.save(user); 
 userService.setRole(user.getUsername(), roleName); 
 return "redirect:/login"; 
 }
}

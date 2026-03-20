package com.example.demo.controller;

import com.example.demo.model.Order;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderHistoryController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    public String listOrders(Model model) {
        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("orders", orders);
        return "order-history";
    }

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại: " + id));
        model.addAttribute("order", order);
        return "order-detail";
    }
}

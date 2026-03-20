package com.example.demo.controller;

import com.example.demo.model.CartItem;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.Product;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    private static final String CART_SESSION_KEY = "cart";

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    /** Thêm sản phẩm vào giỏ hàng (từ form Mua ngay) */
    @PostMapping("/add")
    public String addToCart(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") int quantity,
            HttpSession session,
            RedirectAttributes ra) {

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            ra.addFlashAttribute("error", "Sản phẩm không tồn tại!");
            return "redirect:/products";
        }

        double actualPrice = product.getPrice();
        if (product.getDiscountPercentage() > 0) {
            actualPrice = product.getPrice() - (product.getPrice() * product.getDiscountPercentage() / 100);
        }

        List<CartItem> cart = getCart(session);

        // Kiểm tra giới hạn số lượng khuyến mãi
        if (product.getDiscountPercentage() > 0 && product.getPromoQuantity() != null && product.getPromoQuantity() > 0) {
            int currentQty = 0;
            for (CartItem currentItem : cart) {
                if (currentItem.getProductId().equals(productId)) {
                    currentQty = currentItem.getQuantity();
                    break;
                }
            }
            if (currentQty + quantity > product.getPromoQuantity()) {
                ra.addFlashAttribute("error", "Sản phẩm khuyến mãi chỉ được mua tối đa " + product.getPromoQuantity() + " cái!");
                return "redirect:/products";
            }
        }

        // Nếu đã có sản phẩm này thì tăng số lượng
        for (CartItem item : cart) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                ra.addFlashAttribute("success", "Đã cập nhật số lượng trong giỏ hàng!");
                return "redirect:/cart";
            }
        }

        CartItem item = new CartItem(
                productId, product.getName(), product.getImage(),
                actualPrice, quantity);
        cart.add(item);
        ra.addFlashAttribute("success", "Đã thêm vào giỏ hàng!");
        return "redirect:/cart";
    }

    /** Hiển thị giỏ hàng */
    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);
        double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
        model.addAttribute("cartItems", cart);
        model.addAttribute("cartTotal", total);
        return "cart";
    }

    /** Cập nhật số lượng */
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateQuantity(
            @RequestParam("index") int index,
            @RequestParam("quantity") int quantity,
            HttpSession session) {
        List<CartItem> cart = getCart(session);
        if (index >= 0 && index < cart.size()) {
            CartItem item = cart.get(index);
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            
            if (product != null && product.getDiscountPercentage() > 0 && product.getPromoQuantity() != null && product.getPromoQuantity() > 0) {
                if (quantity > product.getPromoQuantity()) {
                    return ResponseEntity.badRequest().body("Sản phẩm khuyến mãi chỉ được mua tối đa " + product.getPromoQuantity() + " cái!");
                }
            }

            if (quantity <= 0) {
                cart.remove(index);
            } else {
                item.setQuantity(quantity);
            }
        }
        return ResponseEntity.ok().build();
    }

    /** Xóa một sản phẩm khỏi giỏ */
    @GetMapping("/remove/{index}")
    public String removeItem(@PathVariable("index") int index, HttpSession session) {
        List<CartItem> cart = getCart(session);
        if (index >= 0 && index < cart.size()) {
            cart.remove(index);
        }
        return "redirect:/cart";
    }

    /** Thanh toán – Chuyển sang MoMo Test */
    @PostMapping("/checkout")
    public String checkout(@RequestParam(value = "indices", required = false) List<Integer> indices,
                          @RequestParam("customerName") String customerName,
                          @RequestParam("customerPhone") String customerPhone,
                          @RequestParam("customerAddress") String customerAddress,
                          HttpSession session, RedirectAttributes ra) {
        List<CartItem> cart = getCart(session);
        if (cart.isEmpty()) {
            ra.addFlashAttribute("error", "Giỏ hàng trống!");
            return "redirect:/cart";
        }
        if (indices == null || indices.isEmpty()) {
            ra.addFlashAttribute("error", "Vui lòng chọn ít nhất một sản phẩm để thanh toán!");
            return "redirect:/cart";
        }
        
        if (customerName.trim().isEmpty() || customerPhone.trim().isEmpty()) {
            ra.addFlashAttribute("error", "Vui lòng cung cấp đầy đủ thông tin người mua!");
            return "redirect:/cart";
        }

        // 1. Tính toán giá trị thanh toán cho các sản phẩm được chọn
        double selectedSubtotal = 0;
        int selectedItemsCount = 0;
        List<CartItem> selectedItems = new ArrayList<>();
        
        // Sắp xếp indices giảm dần để tránh lệch khi lấy dữ liệu nếu cần, 
        // nhưng ở đây chúng ta chỉ cần tính tổng nên không cần xóa ngay
        for (Integer idx : indices) {
            if (idx >= 0 && idx < cart.size()) {
                CartItem item = cart.get(idx);
                selectedSubtotal += item.getSubtotal();
                selectedItemsCount += item.getQuantity();
                selectedItems.add(item);
            }
        }

        // 2. Tính phí ship (Logic giống JavaScript)
        int shippingFee = 30000;
        if (selectedSubtotal >= 1000000 && selectedItemsCount >= 2) {
            shippingFee = 0;
        }

        long finalAmount = (long) (selectedSubtotal + shippingFee);
        int points = (int) (selectedSubtotal / 7500);
        String orderId = UUID.randomUUID().toString();

        // 3. Gọi MoMo TRƯỚC — Chỉ xóa giỏ sau khi MoMo phản hồi thành công
        try {
            // Thông tin MoMo Sandbox (V1 API)
            String endpoint   = "https://test-payment.momo.vn/gw_payment/transactionProcessor";
            String partnerCode = "MOMO";
            String accessKey   = "F8BBA842ECF85";
            String secretKey   = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
            String requestType = "captureMoMoWallet";
            String returnUrl   = "http://localhost:8080/products";
            String notifyUrl   = "http://localhost:8080/products";
            String orderInfo   = "Thanh toan don hang The Gioi Di Dong cho " + customerName;
            String requestId   = orderId; // dùng chung orderId
            String extraData   = "";
            // V1: amount là String
            String amountStr   = String.valueOf(finalAmount);

            // Signature V1 format (theo thứ tự cố định, không có ipnUrl)
            String rawSignature =
                    "partnerCode=" + partnerCode +
                    "&accessKey="  + accessKey  +
                    "&requestId="  + requestId  +
                    "&amount="     + amountStr  +
                    "&orderId="    + orderId    +
                    "&orderInfo="  + orderInfo  +
                    "&returnUrl="  + returnUrl  +
                    "&notifyUrl="  + notifyUrl  +
                    "&extraData="  + extraData;

            String signature = hmacSha256(rawSignature, secretKey);

            // Body request V1
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("partnerCode", partnerCode);
            requestBody.put("accessKey",   accessKey);
            requestBody.put("requestId",   requestId);
            requestBody.put("amount",      amountStr);
            requestBody.put("orderId",     orderId);
            requestBody.put("orderInfo",   orderInfo);
            requestBody.put("returnUrl",   returnUrl);
            requestBody.put("notifyUrl",   notifyUrl);
            requestBody.put("extraData",   extraData);
            requestBody.put("requestType", requestType);
            requestBody.put("signature",   signature);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            Map<String, Object> response = restTemplate.postForObject(endpoint, entity, Map.class);

            if (response != null && response.containsKey("payUrl")
                    && response.get("payUrl") != null
                    && !response.get("payUrl").toString().isEmpty()) {

                // Lưu lịch sử đơn hàng vào Database
                Order order = new Order();
                order.setOrderId(orderId);
                order.setCustomerName(customerName);
                order.setCustomerPhone(customerPhone);
                order.setCustomerAddress(customerAddress);
                order.setTotalAmount(selectedSubtotal);
                order.setShippingFee(shippingFee);
                order.setGrandTotal(finalAmount);
                order.setLoyaltyPoints(points);
                order.setStatus("Đang xử lý");
                order.setPaymentMethod("MoMo");

                for (CartItem ci : selectedItems) {
                    OrderItem oi = new OrderItem();
                    oi.setOrder(order);
                    oi.setProductName(ci.getProductName());
                    oi.setProductImage(ci.getProductImage());
                    oi.setUnitPrice(ci.getUnitPrice());
                    oi.setQuantity(ci.getQuantity());
                    oi.setSubtotal(ci.getSubtotal());
                    order.getItems().add(oi);
                    
                    // Giảm số lượng khuyến mãi của sản phẩm (nếu có)
                    productRepository.findById(ci.getProductId()).ifPresent(product -> {
                        if (product.getDiscountPercentage() > 0 && product.getPromoQuantity() != null && product.getPromoQuantity() > 0) {
                            int newQty = product.getPromoQuantity() - ci.getQuantity();
                            product.setPromoQuantity(Math.max(0, newQty));
                            productRepository.save(product);
                        }
                    });
                }
                orderRepository.save(order);

                // Chỉ xóa giỏ hàng SAU KHI MoMo phản hồi thành công
                final List<CartItem> cartRef = cart;
                indices.stream().sorted((a, b) -> b - a).forEach(idx -> {
                    if (idx >= 0 && idx < cartRef.size()) {
                        cartRef.remove(idx.intValue());
                    }
                });

                return "redirect:" + response.get("payUrl").toString();
            } else {
                String msg = (response != null && response.get("message") != null)
                        ? response.get("message").toString()
                        : "Không nhận được phản hồi từ MoMo";
                ra.addFlashAttribute("error", "Lỗi từ MoMo: " + msg);
                return "redirect:/cart";
            }

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi kết nối MoMo: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    private String hmacSha256(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(rawHmac);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

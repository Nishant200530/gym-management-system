package com.example.GymManagement.controller;

import com.example.GymManagement.dto.CheckoutDto;
import com.example.GymManagement.model.Order;
import com.example.GymManagement.model.OrderItem;
import com.example.GymManagement.model.Product;
import com.example.GymManagement.model.User;
import com.example.GymManagement.repository.OrderRepository;
import com.example.GymManagement.repository.ProductRepository;
import com.example.GymManagement.service.EmailService;
import com.razorpay.RazorpayClient;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EmailService emailService;

    @Value("${razorpay.key.id}")
    private String razorpayKey;

    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

    // ================= CHECKOUT PAGE =================
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            session.setAttribute("redirectAfterLogin", "/checkout");
            return "redirect:/login";
        }

        List<OrderItem> cart =
                (List<OrderItem>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("cartItems", cart);
        model.addAttribute("checkout", new CheckoutDto());
        return "checkout";
    }

    // ================= SAVE ADDRESS =================
    @PostMapping("/checkout")
    public String saveAddress(
            @Valid @ModelAttribute("checkout") CheckoutDto checkout,
            BindingResult result,
            HttpSession session,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("cartItems", session.getAttribute("cart"));
            return "checkout";
        }

        session.setAttribute("fullName", checkout.getFullName());
        session.setAttribute("mobile", checkout.getMobile());
        session.setAttribute("address", checkout.getAddress());
        session.setAttribute("city", checkout.getCity());
        session.setAttribute("pincode", checkout.getPincode());

        return "redirect:/store/payment";
    }

    // ================= PAYMENT PAGE =================
    @GetMapping("/store/payment")
    public String storePayment(HttpSession session) {
        if (session.getAttribute("loggedUser") == null) {
            session.setAttribute("redirectAfterLogin", "/checkout");
            return "redirect:/login";
        }
        return "store-payment";
    }

    // ================= COD ORDER =================
    @PostMapping("/store/place-order")
    public String placeCODOrder(@RequestParam String paymentMethod,
                                HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");
        List<OrderItem> cart =
                (List<OrderItem>) session.getAttribute("cart");

        Order order = buildOrder(user, cart, session, "COD", "PLACED");
        orderRepository.save(order);

        emailService.sendOrderConfirmationEmail(order);
        session.removeAttribute("cart");

        return "redirect:/store/order-success?orderId=" + order.getId();
    }

    // ================= CREATE RAZORPAY ORDER =================
    @PostMapping("/store/online/create-order")
    @ResponseBody
    public Map<String, Object> createOnlineOrder(HttpSession session) throws Exception {

        List<OrderItem> cart =
                (List<OrderItem>) session.getAttribute("cart");

        double total = cart.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        RazorpayClient client =
                new RazorpayClient(razorpayKey, razorpaySecret);

        JSONObject options = new JSONObject();
        options.put("amount", (int) (total * 100));
        options.put("currency", "INR");
        options.put("receipt", "store_" + System.currentTimeMillis());

        com.razorpay.Order rpOrder =
                client.orders.create(options);

        Map<String, Object> response = new HashMap<>();
        response.put("key", razorpayKey);
        response.put("orderId", rpOrder.get("id"));
        response.put("amount", rpOrder.get("amount"));

        return response;
    }

    // ================= VERIFY RAZORPAY PAYMENT =================
    @PostMapping("/store/online/verify")
    @ResponseBody
    public String verifyOnlinePayment(@RequestBody Map<String, String> payload,
                                      HttpSession session) throws Exception {

        String razorpayOrderId = payload.get("razorpay_order_id");
        String razorpayPaymentId = payload.get("razorpay_payment_id");
        String razorpaySignature = payload.get("razorpay_signature");

        String data = razorpayOrderId + "|" + razorpayPaymentId;

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(
                razorpaySecret.getBytes(), "HmacSHA256"));
        byte[] hash = mac.doFinal(data.getBytes());

        StringBuilder generatedSignature = new StringBuilder();
        for (byte b : hash) {
            generatedSignature.append(
                    String.format("%02x", b));
        }

        if (!generatedSignature.toString()
                .equals(razorpaySignature)) {
            return "FAILED";
        }

        User user =
                (User) session.getAttribute("loggedUser");

        List<OrderItem> cart =
                (List<OrderItem>) session.getAttribute("cart");

        Order order = buildOrder(
                user, cart, session, "ONLINE", "PAID");

        orderRepository.save(order);
        emailService.sendOrderConfirmationEmail(order);
        session.removeAttribute("cart");

        return String.valueOf(order.getId());
    }

    // ================= SUCCESS PAGE =================
    @GetMapping("/store/order-success")
    public String orderSuccess(@RequestParam Long orderId,
                               Model model) {

        model.addAttribute(
                "order",
                orderRepository.findById(orderId).orElse(null));

        return "store-order-success";
    }

    // ================= COMMON ORDER BUILDER =================
    private Order buildOrder(User user,
                             List<OrderItem> cart,
                             HttpSession session,
                             String method,
                             String status) {

        Order order = new Order();
        order.setUsername(user.getEmail());
        order.setEmail(user.getEmail());
        order.setAccountName(user.getName());
        order.setPaymentMethod(method);
        order.setStatus(status);
        order.setOrderDate(LocalDateTime.now());

        order.setFullName((String) session.getAttribute("fullName"));
        order.setMobile((String) session.getAttribute("mobile"));
        order.setAddress((String) session.getAttribute("address"));
        order.setCity((String) session.getAttribute("city"));
        order.setPincode((String) session.getAttribute("pincode"));

        List<OrderItem> items = new ArrayList<>();
        double total = 0;

        for (OrderItem ci : cart) {

            Product product = productRepository
                    .findById(ci.getProductId())
                    .orElseThrow();

            //  STOCK SAFETY
            if (ci.getQuantity() > product.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for " + product.getName());
            }

            //  REDUCE STOCK
            product.setQuantity(
                    product.getQuantity() - ci.getQuantity());
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setProductId(ci.getProductId());
            item.setProductName(ci.getProductName());
            item.setPrice(ci.getPrice());
            item.setQuantity(ci.getQuantity());
            item.setImage(ci.getImage());
            item.setOrder(order);

            total += ci.getPrice() * ci.getQuantity();
            items.add(item);
        }

        order.setItems(items);
        order.setTotalAmount(total);
        return order;
    }
}

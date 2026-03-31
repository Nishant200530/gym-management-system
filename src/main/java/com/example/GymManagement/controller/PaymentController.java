package com.example.GymManagement.controller;

import com.example.GymManagement.model.Payment;
import com.example.GymManagement.model.User;
import com.example.GymManagement.repository.PaymentRepository;
import com.example.GymManagement.repository.UserRepository;
import com.razorpay.RazorpayClient;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepo;

    @Autowired
    private UserRepository userRepo;

    @Value("${razorpay.key.id}")
    private String razorpayKey;

    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

    // ================= SHOW PAYMENT PAGE =================
    @GetMapping("/payment")
    public String paymentPage(
            @RequestParam String plan,
            @RequestParam Double price,
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        model.addAttribute("plan", plan);
        model.addAttribute("price", price);
        return "payment";
    }

    // ================= CREATE RAZORPAY ORDER =================
    @PostMapping("/membership/online/create-order")
    @ResponseBody
    public Map<String, Object> createMembershipOrder(
            @RequestParam Double amount) throws Exception {

        RazorpayClient client =
                new RazorpayClient(razorpayKey, razorpaySecret);

        JSONObject options = new JSONObject();
        options.put("amount", (int) (amount * 100));
        options.put("currency", "INR");
        options.put("receipt", "membership_" + System.currentTimeMillis());

        com.razorpay.Order order =
                client.orders.create(options);

        Map<String, Object> response = new HashMap<>();
        response.put("key", razorpayKey);
        response.put("orderId", order.get("id"));
        response.put("amount", order.get("amount"));

        return response;
    }

    // ================= VERIFY PAYMENT =================
    @PostMapping("/membership/online/verify")
    @ResponseBody
    public String verifyMembershipPayment(
            @RequestBody Map<String, String> payload,
            HttpSession session) throws Exception {

        String orderId = payload.get("razorpay_order_id");
        String paymentId = payload.get("razorpay_payment_id");
        String signature = payload.get("razorpay_signature");
        String plan = payload.get("plan");
        Double amount = Double.valueOf(payload.get("amount"));

        String data = orderId + "|" + paymentId;

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(
                razorpaySecret.getBytes(), "HmacSHA256"));
        byte[] hash = mac.doFinal(data.getBytes());

        StringBuilder generated = new StringBuilder();
        for (byte b : hash) {
            generated.append(String.format("%02x", b));
        }

        if (!generated.toString().equals(signature)) {
            return "FAILED";
        }

        User user =
                (User) session.getAttribute("loggedUser");

        // save payment
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setTransactionId(paymentId);
        payment.setAmount(amount);
        payment.setPlanName(plan);
        payment.setStatus("SUCCESS");
        payment.setPaidAt(LocalDateTime.now());
        payment.setUser(user);
        paymentRepo.save(payment);

        // activate membership
        user.setMembershipPlan(plan);
        user.setMembershipPrice(amount);
        userRepo.save(user);
        session.setAttribute("loggedUser", user);

        return "SUCCESS";
    }
}

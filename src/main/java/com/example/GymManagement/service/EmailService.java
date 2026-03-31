package com.example.GymManagement.service;

import com.example.GymManagement.model.Order;
import com.example.GymManagement.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // ================= OTP EMAIL =================
    public void sendOtp(String email, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Muscle Prime Gym - Password Reset OTP");
        message.setText(
                "Hello,\n\n" +
                "Your OTP for password reset is: " + otp + "\n" +
                "This OTP is valid for 5 minutes.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "— Team Muscle Prime Gym"
        );

        mailSender.send(message);
    }

    // ================= ORDER CONFIRMATION EMAIL =================
    @Async
    public void sendOrderConfirmationEmail(Order order) {

        StringBuilder productDetails = new StringBuilder();

        productDetails.append("ORDER ITEMS\n");
        productDetails.append("---------------------------------\n");

        for (OrderItem item : order.getItems()) {
            productDetails.append("• ")
                    .append(item.getProductName())
                    .append(" | Qty: ")
                    .append(item.getQuantity())
                    .append(" | Rs. ")
                    .append(item.getPrice())
                    .append("\n");
        }

        productDetails.append("---------------------------------\n");

        
        String paymentMethodText;
        if ("COD".equalsIgnoreCase(order.getPaymentMethod())) {
            paymentMethodText = "Cash on Delivery";
        } else {
            paymentMethodText = "Online Payment (Razorpay)";
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(order.getEmail());
        message.setSubject("Order Confirmed | Muscle Prime Gym");

        message.setText(
                "Hello " + order.getFullName() + ",\n\n" +

                "Your order has been placed successfully at Muscle Prime Gym.\n\n" +

                productDetails.toString() + "\n" +

                "ORDER SUMMARY\n" +
                "Order ID: " + order.getId() + "\n" +
                "Payment Method: " + paymentMethodText + "\n" +
                "Total Amount: Rs. " + order.getTotalAmount() + "\n\n" +

                "DELIVERY ADDRESS\n" +
                order.getAddress() + ", " +
                order.getCity() + " - " +
                order.getPincode() + "\n\n" +

                "Account Email: " + order.getEmail() + "\n\n" +

                "Expected Delivery: 3 to 5 working days\n\n" +

                "Thank you for choosing Muscle Prime Gym 💪\n" +
                "Train Hard. Stay Strong.\n\n" +
                "— Team Muscle Prime Gym"
        );

        mailSender.send(message);
    }
}

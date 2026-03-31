package com.example.GymManagement.controller;

import com.example.GymManagement.model.User;
import com.example.GymManagement.repository.UserRepository;
import com.example.GymManagement.repository.PaymentRepository;
import com.example.GymManagement.service.EmailService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // 🔥 ADDED
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ================= LOGIN =================
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // ================= REGISTER =================
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "register";
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email already registered");
            return "register";
        }

        user.setJoinedAt(LocalDateTime.now());
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return "redirect:/login";
    }

    // ================= EDIT PROFILE =================
    @GetMapping("/edit-profile")
    public String editProfile(Model model, HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "edit-profile";
    }

    // ================= UPDATE PROFILE =================
    @PostMapping("/edit-profile")
    public String updateProfile(
            @RequestParam String name,
            @RequestParam String phone,
            HttpSession session
    ) {
        User user = (User) session.getAttribute("loggedUser");

        if (user == null) {
            return "redirect:/login";
        }

        user.setName(name);
        user.setPhone(phone);
        userRepository.save(user);

        session.setAttribute("loggedUser", user);
        return "redirect:/profile";
    }

    // ================= DELETE ACCOUNT =================
    @PostMapping("/delete-account")
    @Transactional   // 🔥 THIS FIXES EVERYTHING
    public String deleteAccount(HttpSession session) {

        User user = (User) session.getAttribute("loggedUser");

        if (user == null) {
            return "redirect:/login";
        }

        // delete child records first
        paymentRepository.deleteByUserId(user.getId());

        // then delete user
        userRepository.deleteById(user.getId());

        session.invalidate();
        return "redirect:/register";
    }

    // ================= AVATAR UPLOAD =================
    @PostMapping("/upload-avatar")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile file,
                               HttpSession session) throws IOException {

        User user = (User) session.getAttribute("loggedUser");

        if (user == null || file.isEmpty()) {
            return "redirect:/login";
        }

        String uploadDir = "C:/Users/91790/Desktop/uploads/";
        Files.createDirectories(Paths.get(uploadDir));

        String fileName = "user_" + user.getId() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + fileName);

        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        user.setAvatar(fileName);
        userRepository.save(user);
        session.setAttribute("loggedUser", user);

        if ("ROLE_ADMIN".equals(user.getRole())) {
            return "redirect:/admin/dashboard";
        }

        return "redirect:/profile";
    }

    // ================= LOGOUT =================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // ================= FORGOT PASSWORD =================
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String email, Model model) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            model.addAttribute("error", "Email not registered");
            return "forgot-password";
        }

        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        emailService.sendOtp(email, otp);

        model.addAttribute("email", email);
        return "verify-otp";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String password,
            Model model) {

        User user = userRepository.findByEmail(email);

        if (user == null ||
                !otp.equals(user.getOtp()) ||
                user.getOtpExpiry().isBefore(LocalDateTime.now())) {

            model.addAttribute("error", "Invalid or expired OTP");
            model.addAttribute("email", email);
            return "verify-otp";
        }

        user.setPassword(passwordEncoder.encode(password));
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        return "redirect:/login";
    }
}

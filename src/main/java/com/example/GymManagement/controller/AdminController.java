package com.example.GymManagement.controller;

import com.example.GymManagement.model.User;
import com.example.GymManagement.repository.UserRepository;
import com.example.GymManagement.repository.PaymentRepository;
import com.example.GymManagement.repository.ContactRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ContactRepository contactRepository;

    // ================= ADMIN DASHBOARD =================
    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    // ================= MEMBERSHIP (REDIRECT ONLY) =================
    @GetMapping("/membership")
    public String membershipPage() {
        // MembershipController owns the data
        return "redirect:/admin/memberships";
    }

    // ================= USERS =================
    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("totalUsers", userRepository.count());
        return "admin-users";
    }

    // ================= PAYMENTS =================
    @GetMapping("/payments")
    public String viewPayments(Model model) {
        model.addAttribute("payments", paymentRepository.findAll());
        return "admin-payments";
    }

    // ================= QUERIES =================
    @GetMapping("/queries")
    public String viewUserQueries(Model model) {
        model.addAttribute("queries", contactRepository.findAll());
        return "admin-queries";
    }

    @PostMapping("/delete-query/{id}")
    public String deleteQuery(@PathVariable int id) {
        contactRepository.deleteById(id);
        return "redirect:/admin/queries";
    }
}

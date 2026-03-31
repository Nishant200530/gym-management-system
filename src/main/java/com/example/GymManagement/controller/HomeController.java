package com.example.GymManagement.controller;

import com.example.GymManagement.model.User;
import com.example.GymManagement.repository.MembershipRepository;
import com.example.GymManagement.repository.TrainerRepository;
import com.example.GymManagement.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    // ================= HOME =================

    // root url
    @GetMapping("/")
    public String rootHome() {
        return "home";
    }

    // FIX: user login redirect target
    @GetMapping("/home")
    public String home() {
        return "home";
    }

    // ================= STATIC PAGES =================

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/trainers")
    public String trainers(Model model) {
        model.addAttribute("trainers", trainerRepository.findAll());
        return "trainers";
    }

    @GetMapping("/membership")
    public String membership(Model model) {
        model.addAttribute("memberships", membershipRepository.findAll());
        return "membership";
    }

    // ================= PROFILE =================

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // admin → admin dashboard
        if (isAdmin) {
            return "redirect:/admin/dashboard";
        }

        // user → profile
        String email = auth.getName(); // username = email
        User user = userRepository.findByEmail(email);

        session.setAttribute("loggedUser", user);
        model.addAttribute("user", user);

        return "profile";
    }
}

package com.example.GymManagement.controller;

import com.example.GymManagement.model.User;
import com.example.GymManagement.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostLoginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/post-login")
    public String postLogin(HttpSession session) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email);

        //  keep session in sync
        session.setAttribute("loggedUser", user);

        //  ROLE-BASED REDIRECT
        if ("ROLE_ADMIN".equals(user.getRole())) {
            return "redirect:/admin/dashboard";
        }

        //  USER → HOME PAGE 
        return "redirect:/home";
    }
}

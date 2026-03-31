package com.example.GymManagement.controller;

import com.example.GymManagement.model.Membership;
import com.example.GymManagement.repository.MembershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MembershipController {

    @Autowired
    private MembershipRepository membershipRepository;

    // ================= ADMIN CRUD =================

    @GetMapping("/admin/memberships")
    public String adminMemberships(Model model) {
        model.addAttribute("membership", new Membership());
        model.addAttribute("memberships", membershipRepository.findAll());
        return "admin-membership";
    }

    @PostMapping("/admin/membership/save")
    public String saveMembership(@ModelAttribute Membership membership) {
        membershipRepository.save(membership);
        return "redirect:/admin/memberships";
    }

    @GetMapping("/admin/membership/edit/{id}")
    public String editMembership(@PathVariable int id, Model model) {
        model.addAttribute("membership",
                membershipRepository.findById(id).orElse(null));
        model.addAttribute("memberships",
                membershipRepository.findAll());
        return "admin-membership";
    }

    @GetMapping("/admin/membership/delete/{id}")
    public String deleteMembership(@PathVariable int id) {
        membershipRepository.deleteById(id);
        return "redirect:/admin/memberships";
    }
}

package com.example.GymManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.GymManagement.model.ContactMessage;
import com.example.GymManagement.repository.ContactRepository;

import java.time.LocalDateTime;

@Controller
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @PostMapping("/contact")
    public String saveMessage(@ModelAttribute ContactMessage message) {

        //  ADD TIME (new feature)
        message.setSentAt(LocalDateTime.now());

        contactRepository.save(message);
        return "redirect:/contact";
    }
}

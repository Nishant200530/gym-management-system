package com.example.GymManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailTestController {

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/test-mail")
    public String testMail() {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("yourpersonalemail@gmail.com"); 
        msg.setSubject("Gym Management Test Mail");
        msg.setText("Mail system is working fine.");

        mailSender.send(msg);

        return "Mail sent successfully";
    }
}

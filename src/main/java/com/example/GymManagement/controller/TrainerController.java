package com.example.GymManagement.controller;

import com.example.GymManagement.model.Trainer;
import com.example.GymManagement.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/trainers")
public class TrainerController {

    @Autowired
    private TrainerRepository trainerRepository;

    @GetMapping
    public String adminTrainers(Model model) {
        model.addAttribute("trainers", trainerRepository.findAll());
        model.addAttribute("trainer", new Trainer());
        return "admin-trainers";
    }

    @PostMapping("/add")
    public String addTrainer(@ModelAttribute Trainer trainer) {
        trainerRepository.save(trainer);
        return "redirect:/admin/trainers";
    }

    @GetMapping("/delete/{id}")
    public String deleteTrainer(@PathVariable int id) {
        trainerRepository.deleteById(id);
        return "redirect:/admin/trainers";
    }
}

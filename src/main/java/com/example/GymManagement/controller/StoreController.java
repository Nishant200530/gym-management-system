package com.example.GymManagement.controller;

import com.example.GymManagement.model.Product;
import com.example.GymManagement.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class StoreController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/store")
    public String showStorePage(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "store";
    }

    // ================= ADD TO CART =================
    @GetMapping("/add-to-cart/{id}")
    public String addToCart(@PathVariable Long id, HttpSession session) {

        List<Product> cart = (List<Product>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        productRepository.findById(id).ifPresent(cart::add);
        session.setAttribute("cart", cart);

        return "redirect:/store";
    }

    // ================= BUY NOW FLOW =================
    @GetMapping("/buy-now/{id}")
    public String buyNow(@PathVariable Long id, HttpSession session) {

        // 🔐 LOGIN CHECK
        if (session.getAttribute("loggedUser") == null) {
            session.setAttribute("redirectAfterLogin", "/buy-now/" + id);
            return "redirect:/login";
        }

        //  CREATE SINGLE ITEM CART
        List<Product> cart = new ArrayList<>();
        productRepository.findById(id).ifPresent(cart::add);
        session.setAttribute("cart", cart);

        return "redirect:/checkout";
    }

    // ================= STORE PAYMENT PAGE =================
    @GetMapping("/store-payment")
    public String showStorePaymentPage(Model model) {
        model.addAttribute("userId", 1);
        return "store-payment";
    }
}

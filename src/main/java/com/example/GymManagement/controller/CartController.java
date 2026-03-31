package com.example.GymManagement.controller;

import com.example.GymManagement.model.OrderItem;
import com.example.GymManagement.model.Product;
import com.example.GymManagement.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private ProductRepository productRepository;

    // ================= ADD TO CART =================
    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam int quantity,
                            HttpSession session) {

        List<OrderItem> cart =
                (List<OrderItem>) session.getAttribute("cart");

        if (cart == null) cart = new ArrayList<>();

        Product p = productRepository.findById(productId).orElse(null);
        if (p != null) {

            OrderItem item = new OrderItem();
            item.setProductId(p.getId());
            item.setProductName(p.getName());
            item.setPrice(p.getPrice());

            int finalQty = Math.min(quantity, p.getQuantity());
            item.setQuantity(finalQty);

            item.setImage(p.getImage());
            cart.add(item);
        }

        session.setAttribute("cart", cart);
        return "redirect:/store";
    }

    // ================= BUY NOW =================
    @PostMapping("/buy-now-checkout")
    public String buyNowCheckout(@RequestParam Long productId,
                                 @RequestParam int quantity,
                                 HttpSession session) {

        List<OrderItem> cart = new ArrayList<>();

        Product p = productRepository.findById(productId).orElse(null);
        if (p != null) {

            OrderItem item = new OrderItem();
            item.setProductId(p.getId());
            item.setProductName(p.getName());
            item.setPrice(p.getPrice());

            int finalQty = Math.min(quantity, p.getQuantity());
            item.setQuantity(finalQty);

            item.setImage(p.getImage());
            cart.add(item);
        }

        session.setAttribute("cart", cart);
        return "redirect:/checkout";
    }

    // ================= SHOW CART =================
    @GetMapping("/cart")
    public String showCart(Model model, HttpSession session) {

        List<OrderItem> cart =
                (List<OrderItem>) session.getAttribute("cart");

        if (cart == null) cart = new ArrayList<>();

        model.addAttribute("cartItems", cart);
        return "cart";
    }

    // ================= UPDATE QUANTITY =================
    @PostMapping("/cart/update")
    public String updateQuantity(@RequestParam int index,
                                 @RequestParam int quantity,
                                 HttpSession session) {

        List<OrderItem> cart =
                (List<OrderItem>) session.getAttribute("cart");

        if (cart != null && index < cart.size()) {
            if (quantity > 0) {
                cart.get(index).setQuantity(quantity);
            }
        }

        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // ================= REMOVE ITEM =================
    @PostMapping("/cart/remove")
    public String removeItem(@RequestParam int index,
                             HttpSession session) {

        List<OrderItem> cart =
                (List<OrderItem>) session.getAttribute("cart");

        if (cart != null && index < cart.size()) {
            cart.remove(index);
        }

        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }
}

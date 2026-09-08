package com.EnterpriseWarehouseManagementSystem.Lab3Grupp5;

import com.EnterpriseWarehouseManagementSystem.Lab3Grupp5.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    private final ProductService productService;

    public WebController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String homePage(Model model) {

        model.addAttribute("products", productService.getAllProducts());
        return "index";
    }
}
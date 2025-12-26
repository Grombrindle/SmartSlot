package com.appointment.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    @GetMapping("/register/customer")
    public String registerCustomer() {
        return "register-customer"; // You can create this template later
    }
    
    @GetMapping("/register/staff")
    public String registerStaff() {
        return "register-staff"; // You can create this template later
    }
}
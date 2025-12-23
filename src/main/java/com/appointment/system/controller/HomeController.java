package com.appointment.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "home"; // This would render home.html if you have templates
        // OR redirect to a specific page:
        // return "redirect:/api/appointments";
    }
    
    // Optional: A public welcome page before login
    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }
}
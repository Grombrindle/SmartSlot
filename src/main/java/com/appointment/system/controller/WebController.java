// package com.appointment.system.controller;

// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;

// @Controller
// @RequestMapping("/")
// public class WebController {

//     @GetMapping("/login")
//     public String loginPage() {
//         return "auth/login";
//     }

//     @GetMapping("/register")
//     public String registerPage() {
//         return "auth/register";
//     }

//     @GetMapping("/dashboard")
//     public String dashboard() {
//         // This will redirect to role-specific dashboard based on user role
//         return "redirect:/dashboard/redirect";
//     }

//     @GetMapping("/dashboard/redirect")
//     public String redirectToRoleDashboard() {
//         // This will be handled by Spring Security's authentication
//         // Thymeleaf will handle role-based rendering
//         return "dashboards/dashboard";
//     }

//     @GetMapping("/dashboard/admin")
//     public String adminDashboard() {
//         return "dashboards/admin";
//     }

//     @GetMapping("/dashboard/staff")
//     public String staffDashboard() {
//         return "dashboards/staff";
//     }

//     @GetMapping("/dashboard/customer")
//     public String customerDashboard() {
//         return "dashboards/customer";
//     }
// }
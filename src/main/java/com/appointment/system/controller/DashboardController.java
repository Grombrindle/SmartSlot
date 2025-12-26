package com.appointment.system.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication) {
        if (authentication != null && authentication.getAuthorities() != null) {
            var authorities = authentication.getAuthorities();
            
            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/dashboard/admin";
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"))) {
                return "redirect:/dashboard/staff";
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))) {
                return "redirect:/dashboard/customer";
            }
        }
        return "dashboards/dashboard";
    }
    
    @GetMapping("/dashboard/admin")
    public String showAdminDashboard() {
        return "dashboards/admin-dashboard";
    }
    
    @GetMapping("/dashboard/staff")
    public String showStaffDashboard() {
        return "dashboards/staff-dashboard";
    }
    
    @GetMapping("/dashboard/customer")
    public String showCustomerDashboard() {
        return "dashboards/customer-dashboard";
    }
}
package com.appointment.system.controller;

import com.appointment.system.enums.AppointmentStatus;
import com.appointment.system.enums.UserRole;
import com.appointment.system.model.*;
import com.appointment.system.repository.*;
import com.appointment.system.service.AppointmentService;
import com.appointment.system.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class DashboardController {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final AppointmentRepository appointmentRepository;
    private final WorkingScheduleRepository workingScheduleRepository;
    private final UserService userService;
    private final AppointmentService appointmentService;

    public DashboardController(UserRepository userRepository,
                              ServiceRepository serviceRepository,
                              AppointmentRepository appointmentRepository,
                              WorkingScheduleRepository workingScheduleRepository,
                              UserService userService,
                              AppointmentService appointmentService) {
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.appointmentRepository = appointmentRepository;
        this.workingScheduleRepository = workingScheduleRepository;
        this.userService = userService;
        this.appointmentService = appointmentService;
    }

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
    public String showAdminDashboard(Model model, Authentication authentication) {
        // Get statistics
        long totalUsers = userRepository.count();
        long activeServices = serviceRepository.findByIsActiveTrue().size();
        long pendingAppointments = appointmentRepository.findByStatus(AppointmentStatus.PENDING).size();
        long todaysAppointments = appointmentRepository.findByAppointmentDateTimeBetween(
            LocalDate.now().atStartOfDay(),
            LocalDate.now().plusDays(1).atStartOfDay()
        ).size();
        
        // Get recent appointments
        List<Appointment> recentAppointments = appointmentRepository.findAll().stream()
            .limit(10)
            .toList();
        
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeServices", activeServices);
        model.addAttribute("pendingAppointments", pendingAppointments);
        model.addAttribute("todaysAppointments", todaysAppointments);
        model.addAttribute("recentAppointments", recentAppointments);
        
        return "dashboards/admin-dashboard";
    }
    
    @GetMapping("/dashboard/staff")
    public String showStaffDashboard(Model model, Authentication authentication) {
        String email = authentication.getName();
        User staff = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get today's appointments
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();
        List<Appointment> todaysAppointments = appointmentRepository.findByStaffAndDate(staff, LocalDateTime.now());
        
        model.addAttribute("staff", staff);
        model.addAttribute("todaysAppointments", todaysAppointments);
        
        return "dashboards/staff-dashboard";
    }
    
    @GetMapping("/dashboard/customer")
    public String showCustomerDashboard(Model model, Authentication authentication) {
        String email = authentication.getName();
        User customer = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get upcoming appointments
        List<Appointment> upcomingAppointments = appointmentRepository.findByCustomer(customer).stream()
            .filter(a -> a.getAppointmentDateTime().isAfter(LocalDateTime.now()))
            .limit(5)
            .toList();
        
        // Get active services for booking
        List<Service> activeServices = serviceRepository.findByIsActiveTrue();
        
        model.addAttribute("customer", customer);
        model.addAttribute("upcomingAppointments", upcomingAppointments.size());
        model.addAttribute("services", activeServices);
        
        return "dashboards/customer-dashboard";
    }
    
    // @GetMapping("/admin/users")
    // @PreAuthorize("hasRole('ADMIN')")
    // public String adminUsers(Model model) {
    //     List<User> users = userRepository.findAll();
    //     model.addAttribute("users", users);
    //     return "admin-users";
    // }
    @GetMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public String adminUsers(Model model) {
    List<User> users = userRepository.findAll();
    
    // Pre-compute role counts
    long adminCount = users.stream()
            .filter(u -> u.getRole() == UserRole.ADMIN)
            .count();
    
    long staffCount = users.stream()
            .filter(u -> u.getRole() == UserRole.STAFF)
            .count();
    
    long customerCount = users.stream()
            .filter(u -> u.getRole() == UserRole.CUSTOMER)
            .count();
    
    model.addAttribute("users", users);
    model.addAttribute("adminCount", adminCount);
    model.addAttribute("staffCount", staffCount);
    model.addAttribute("customerCount", customerCount);
    
    return "admin-users";
}
  @GetMapping("/admin/services")
@PreAuthorize("hasRole('ADMIN')")
public String adminServices(Model model) {
    List<Service> services = serviceRepository.findAll();
    List<User> staffMembers = userRepository.findAll().stream()
        .filter(user -> user.getRole().name().equals("STAFF"))
        .toList();
    
    // Pre-compute statistics
    long activeServices = services.stream()
        .filter(Service::isActive)
        .count();
    
    long inactiveServices = services.stream()
        .filter(service -> !service.isActive())
        .count();
    
    model.addAttribute("services", services);
    model.addAttribute("staffMembers", staffMembers);
    model.addAttribute("activeServices", activeServices);  // Add this
    model.addAttribute("inactiveServices", inactiveServices);  // Add this
    
    return "admin-services";
}
    // @GetMapping("/admin/schedules")
    // @PreAuthorize("hasRole('ADMIN')")
    // public String adminSchedules(Model model) {
    //     List<WorkingSchedule> schedules = workingScheduleRepository.findAll();
    //     List<User> staffMembers = userRepository.findAll().stream()
    //         .filter(user -> user.getRole().name().equals("STAFF"))
    //         .toList();
        
    //     model.addAttribute("schedules", schedules);
    //     model.addAttribute("staffMembers", staffMembers);
    //     return "admin-schedules";
    // }
    
    // @GetMapping("/admin/appointments")
    // @PreAuthorize("hasRole('ADMIN')")
    // public String adminAppointments(Model model) {
    //     List<Appointment> appointments = appointmentRepository.findAll();
    //     model.addAttribute("appointments", appointments);
    //     return "admin-appointments";
    // }
    
    // Staff pages
    @GetMapping("/staff/dashboard")
    @PreAuthorize("hasRole('STAFF')")
    public String staffDashboard(Model model, Authentication authentication) {
        String email = authentication.getName();
        User staff = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get today's appointments
        List<Appointment> todaysAppointments = appointmentRepository.findByStaffAndDate(staff, LocalDateTime.now());
        
        model.addAttribute("staff", staff);
        model.addAttribute("todaysAppointments", todaysAppointments);
        
        return "staff-dashboard";
    }
    
    @GetMapping("/staff/schedule")
    @PreAuthorize("hasRole('STAFF')")
    public String staffSchedule(Model model, Authentication authentication) {
        String email = authentication.getName();
        User staff = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<WorkingSchedule> schedules = workingScheduleRepository.findByStaffAndIsHolidayFalse(staff);
        
        model.addAttribute("staff", staff);
        model.addAttribute("schedules", schedules);
        
        return "staff-schedule";
    }
    
    @GetMapping("/staff/appointments")
    @PreAuthorize("hasRole('STAFF')")
    public String staffAppointments(Model model, Authentication authentication) {
        String email = authentication.getName();
        User staff = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Appointment> appointments = appointmentRepository.findByStaff(staff);
        
        model.addAttribute("staff", staff);
        model.addAttribute("appointments", appointments);
        
        return "staff-appointments";
    }
    
    @GetMapping("/staff/availability")
    @PreAuthorize("hasRole('STAFF')")
    public String staffAvailability(Model model, Authentication authentication) {
        String email = authentication.getName();
        User staff = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("staff", staff);
        
        return "staff-availability";
    }
    
    // Customer pages
    @GetMapping("/customer/book")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String customerBook(Model model) {
        List<Service> services = serviceRepository.findByIsActiveTrue();
        List<User> staffMembers = userRepository.findAll().stream()
            .filter(user -> user.getRole().name().equals("STAFF"))
            .toList();
        
        model.addAttribute("services", services);
        model.addAttribute("staffMembers", staffMembers);
        return "customer-book";
    }
    
    @GetMapping("/customer/appointments")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String customerAppointments(Model model, Authentication authentication) {
        String email = authentication.getName();
        User customer = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Appointment> appointments = appointmentRepository.findByCustomer(customer);
        
        model.addAttribute("customer", customer);
        model.addAttribute("appointments", appointments);
        
        return "customer-appointments";
    }
    
    @GetMapping("/customer/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String customerProfile(Model model, Authentication authentication) {
        String email = authentication.getName();
        User customer = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("customer", customer);
        return "customer-profile";
    }
    
    @GetMapping("/customer/history")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String customerHistory(Model model, Authentication authentication) {
        String email = authentication.getName();
        User customer = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Appointment> appointments = appointmentRepository.findByCustomer(customer).stream()
            .filter(a -> a.getAppointmentDateTime().isBefore(LocalDateTime.now()))
            .toList();
        
        model.addAttribute("customer", customer);
        model.addAttribute("appointments", appointments);
        
        return "customer-history";
    }
}